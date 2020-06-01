package main.command;

import java.util.List;

/**
 * 事务类Affair
 * 封装了一次整体（复杂）操作，内含1个或者多个基本命令
 * 例如，clear命令涉及多个shape的remove操作
 * @author Tian Runze
 *
 */
public class Affair {
	private int id; // 事务ID
	private List<Command> commands; // 包含的命令
	
	public Affair(int id, List<Command> commands) {
		this.id = id;
		this.commands = commands;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;		
	}
	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
	public List<Command> getCommands() {
		return this.commands;
	}
	
	
}
