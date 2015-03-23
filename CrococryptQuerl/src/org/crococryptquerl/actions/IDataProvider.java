package org.crococryptquerl.actions;

public interface IDataProvider {
	public DataIn read(String id);
	public DataOut write(String id);
}
