package com.hotel.common;

/**
 * 响应码常量类
 */
public interface ResultCodeConstant {

    /**
     * 成功
     */
    Integer SUCCESS = 200;

    /**
     * 失败
     */
    Integer ERROR = 500;

    /**
     * 参数错误
     */
    Integer BAD_REQUEST = 400;

    /**
     * 未授权
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    Integer FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    Integer NOT_FOUND = 404;

    /**
     * 用户名或密码错误
     */
    ResultCodeConstant USERNAME_OR_PASSWORD_ERROR = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 1001;
        }

        @Override
        public String getMessage() {
            return "用户名或密码错误";
        }
    };

    /**
     * 用户已存在
     */
    ResultCodeConstant USER_ALREADY_EXISTS = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 1002;
        }

        @Override
        public String getMessage() {
            return "用户已存在";
        }
    };

    /**
     * 用户不存在
     */
    ResultCodeConstant USER_NOT_FOUND = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 1003;
        }

        @Override
        public String getMessage() {
            return "用户不存在";
        }
    };

    /**
     * 用户已禁用
     */
    ResultCodeConstant USER_DISABLED = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 1004;
        }

        @Override
        public String getMessage() {
            return "用户已禁用";
        }
    };

    /**
     * Token无效
     */
    ResultCodeConstant TOKEN_INVALID = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 1005;
        }

        @Override
        public String getMessage() {
            return "Token无效或已过期";
        }
    };

    /**
     * 房型已存在
     */
    ResultCodeConstant ROOM_TYPE_ALREADY_EXISTS = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 2001;
        }

        @Override
        public String getMessage() {
            return "房型已存在";
        }
    };

    /**
     * 房型不存在
     */
    ResultCodeConstant ROOM_TYPE_NOT_FOUND = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 2002;
        }

        @Override
        public String getMessage() {
            return "房型不存在";
        }
    };

    /**
     * 房间已存在
     */
    ResultCodeConstant ROOM_ALREADY_EXISTS = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 2003;
        }

        @Override
        public String getMessage() {
            return "房间已存在";
        }
    };

    /**
     * 房间不存在
     */
    ResultCodeConstant ROOM_NOT_FOUND = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 2004;
        }

        @Override
        public String getMessage() {
            return "房间不存在";
        }
    };

    /**
     * 房间不可用
     */
    ResultCodeConstant ROOM_NOT_AVAILABLE = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 2005;
        }

        @Override
        public String getMessage() {
            return "房间不可用";
        }
    };

    /**
     * 订单不存在
     */
    ResultCodeConstant ORDER_NOT_FOUND = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 3001;
        }

        @Override
        public String getMessage() {
            return "订单不存在";
        }
    };

    /**
     * 订单状态错误
     */
    ResultCodeConstant ORDER_STATUS_ERROR = new ResultCodeConstant() {
        @Override
        public Integer getCode() {
            return 3002;
        }

        @Override
        public String getMessage() {
            return "订单状态错误";
        }
    };

    /**
     * 获取响应码
     */
    Integer getCode();

    /**
     * 获取响应消息
     */
    String getMessage();
}
