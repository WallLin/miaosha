package cn.kyrie.miaosha.exception;


import cn.kyrie.miaosha.result.CodeMsg;

/**
 * 全局异常
 * @author kyrie
 * @date 2019-12-25 - 11:40
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -6434803512529943702L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
