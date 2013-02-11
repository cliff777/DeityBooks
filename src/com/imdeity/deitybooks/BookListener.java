package com.imdeity.deitybooks;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityListener;
import com.imdeity.deityapi.records.DatabaseResults;

public class BookListener extends DeityListener {

	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event) {

		if(event.getAction() == Action.LEFT_CLICK_BLOCK && 
				event.getClickedBlock().getType() == Material.BOOKSHELF && 
				event.getPlayer().getItemInHand().getType() == Material.WRITTEN_BOOK && 
				event.getPlayer().getItemInHand().hasItemMeta()) {

			putBookInBookcase(event.getPlayer(), event.getClickedBlock());

		}else if(event.getAction() == Action.RIGHT_CLICK_BLOCK &&
				event.getClickedBlock().getType() == Material.BOOKSHELF &&
				event.getPlayer().getItemInHand().getType() == Material.BOOK &&
				!event.getPlayer().getItemInHand().hasItemMeta()) {
			//You may only copy one book at a time to prevent accidental copying of full stacks of books
			if(event.getPlayer().getItemInHand().getAmount() > 1) {
				DeityAPI.getAPI().getChatAPI().sendPlayerError(event.getPlayer(), "DeityBooks", "You can only copy one book at a time!");
				return;
			}
			copyBook(event.getPlayer(), event.getClickedBlock());
		}
	}

	//When we break an already set-up bookcase, remove all rows containing data
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().getType() == Material.BOOKSHELF) {
			//Drop a written book
			BookMeta meta = DeityBooksDatabase.getBookMeta(event.getBlock());
			if(meta != null) {
				//There is a bookcase with data
				ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
				item.setItemMeta(meta);
				event.getPlayer().getWorld().dropItem(event.getBlock().getLocation(), item);
				
				//Delete the book from the table
				DeityAPI.getAPI().getDataAPI().getMySQL().write("DELETE FROM `bookcases` WHERE `x`=? AND `y`=? AND `z`=?", 
						event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
			}		
		}
	}




	public void putBookInBookcase(Player p, Block b) {
		BookMeta book = (BookMeta) p.getItemInHand().getItemMeta();
		String author = book.getAuthor();
		String title = book.getTitle();
		List<String> pages = book.getPages();
		int bookid = 0;

		//See if we have a book in table and if we do, get the book id, if not, leave it at 0
		DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced("SELECT * FROM `bookcases` ORDER BY `id` DESC LIMIT 1");
		if(query != null && query.hasRows()) {
			try {
				int x, y, z;
				x = query.getInteger(0, "x");
				y = query.getInteger(0, "y");
				z = query.getInteger(0, "z");
				if(x == b.getX() && y == b.getY() && z == b.getZ()) {
					//We already have a book in that bookcase
					DeityAPI.getAPI().getChatAPI().sendPlayerError(p, "DeityBooks", "There is already a book in that bookcase");
					return;
				}
				bookid = query.getInteger(0, "bookid") + 1;
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		}

		//Put all the pages into the table
		for(String page : pages) {
			String sql = "INSERT INTO `bookcases` (`bookid`, `placer`, `x`, `y`, `z`, `title`, `author`, `text`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, bookid, p.getName(), b.getX(), b.getY(), b.getZ(), title, author, page);
		}
		
		DeityBooks.plugin.chat.sendPlayerMessage(p, "&aYou put &b" + title + " &a by &b" + author + " &a in the bookcase");

	}


	//Make a copy of the book in the bookcase and 'give' it to the player
	public void copyBook(Player p, Block b) {
		System.out.println("copy");
		BookMeta meta = DeityBooksDatabase.getBookMeta(b);
		if(meta != null) {
			p.getItemInHand().setType(Material.WRITTEN_BOOK);
			p.getItemInHand().setItemMeta(meta);
		}
		DeityBooks.plugin.chat.sendPlayerMessage(p, "&aYou copied &b" + meta.getTitle() + " &a by &b" + meta.getAuthor());
	}
}
