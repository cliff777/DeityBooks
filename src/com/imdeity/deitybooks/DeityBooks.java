package com.imdeity.deitybooks;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityPlugin;

public class DeityBooks extends DeityPlugin {
	public static DeityBooks plugin;

	protected void initCmds() {
		//No commands
	}

	protected void initConfig() {
		//No needed config
	}

	protected void initDatabase() {
		DeityAPI.getAPI().getDataAPI().getMySQL().write("CREATE TABLE IF NOT EXISTS `bookcases` (`id` INT(16) NOT NULL AUTO_INCREMENT, " +
				"`bookid` INT(16) NOT NULL, `placer` VARCHAR(30) NOT NULL, `x` INT(8) NOT NULL, `y` INT(8) NOT NULL, `z` INT(8) NOT NULL, " +
				"`title` VARCHAR(64) NOT NULL, `author` VARCHAR(30) NOT NULL, `text` TINYTEXT NOT NULL, " +
				"PRIMARY KEY (`id`))");
	}

	protected void initInternalDatamembers() {
		//No datamembers
	}

	protected void initLanguage() {
		//No language
	}

	protected void initListeners() {
		this.registerListener(new BookListener());
	}

	protected void initPlugin() {
		plugin = this;
	}

	protected void initTasks() {
		//No needed tasks
	}

}
