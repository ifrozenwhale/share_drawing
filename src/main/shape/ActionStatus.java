package main.shape;

/**
 * 操作状态枚举,
 * DRAWING 正在画
 * SELECTING 选择中
 * RESHAPING 调整尺寸
 * MOVING 移动
 * MODIFYING 改变样式
 * FILLING 填充颜色
 * REMOVING 移除
 * NONE 等待状态
 */
public enum ActionStatus {
    DRAWING, SELECTING, RESHAPING, MOVING, MODIFYING, FILLING, REMOVING, NONE
}
