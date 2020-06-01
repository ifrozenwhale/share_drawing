package main.command;

import main.shape.Shape;

/**
 * 封装成命令
 * 包含操作类型和操作对象
 */
public class Command {
	private ActionType actionType; // 操作类型
    private Shape shape; // 操作的对象

    public Command(ActionType actionType, Shape shape) {
    	this.actionType = actionType;
        this.shape = shape;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }


}
