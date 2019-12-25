package cn.kyrie.miaosha.exception;

import cn.kyrie.miaosha.result.CodeMsg;
import cn.kyrie.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 * @author kyrie
 * @date 2019-12-25 - 11:26
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<CodeMsg> exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace(); // 将异常信息打印到控制台
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            CodeMsg cm = ex.getCm();
            return Result.error(cm);
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
