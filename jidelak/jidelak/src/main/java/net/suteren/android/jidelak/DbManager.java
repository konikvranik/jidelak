package net.suteren.android.jidelak;

import java.sql.Connection;

public class DbManager {

	/**
	 * Method which upgrades database from old version to new version.
	 * 
	 * @param oldVersion
	 *            version from which are we upgrading
	 * @param newVersion
	 *            version to which we are upgrading
	 * @return number of version after upgrade. -1 if fail.
	 */
	public int upgradeOrCreate(int oldVersion, int newVersion) {
		return -1;
	}

	public int upgradeOrCreate() {
		return upgradeOrCreate(getActualVersion(), getTargetVersion());
	}

	public int getActualVersion() {
		return 0;
	}

	public int getTargetVersion() {
		return 0;
	}

	public Connection getConnection() {
		return null;
	}

}
