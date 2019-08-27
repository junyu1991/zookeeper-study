package com.yujun.zookeeper.test;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:12
 * @description TODO
 **/
public enum TestEnum {
    TEST{
        private String message;
        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return this.message;
        }
    };

    public abstract void setMessage(String message);
    public abstract String getMessage();
}
