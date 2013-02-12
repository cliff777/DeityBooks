package com.imdeity.deitybooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.DatabaseResults;

public class DeityBooksDatabase {
	
	public static BookMeta getBookMeta(Block b) {
		String title, author;
		title = author = null;
		List<String> pages = new ArrayList<String>();
		
		String sql = "SELECT * FROM `bookcases` WHERE `x`=? AND `y`=? AND `z`=?";
		DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql, b.getX(), b.getY(), b.getZ());
		
		if(query != null && query.hasRows()) {
			try {
				//There is indeed a 'book in the bookcase'
				title = query.getString(0, "title");
				author = query.getString(0, "author");

				for(int i = 0; i < query.rowCount(); i++) {
					pages.add(query.getString(i, "text"));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) item.getItemMeta();
			meta.setTitle(title);
			meta.setAuthor(author);
			meta.setPages(pages);
			return meta;
		}
		return null;
	}
}
