package com.ljheee.page.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Connection;
import java.util.Properties;

import static org.apache.ibatis.reflection.SystemMetaObject.*;

/**
 * Created by lijianhua04 on 2018/7/20.
 *
 * https://blog.csdn.net/ykzhen2015/article/details/50349540
 *
 *
 */
@Intercepts(@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
))
public class PageInterceptor implements Interceptor {


    private int limit = 0;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = forObject(statementHandler);

        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = forObject(object);
        }

        //BoundSql对象是处理sql语句的。
        String sql = (String)metaStatementHandler.getValue("delegate.boundSql.sql");

        //判断sql是否select语句，如果不是select语句那么就出错了。
        //如果是修改它，是的它最多返回行，这里用的是mysql，其他数据库要改写成其他
        if (sql != null && sql.toLowerCase().trim().indexOf("select") == 0 && !sql.contains("$_$limit_$table_")) {
            //通过sql重写来实现，这里我们起了一个奇怪的别名，避免表名重复.
            sql = "select * from (" + sql + ") $_$limit_$table_ limit " + this.limit;
            metaStatementHandler.setValue("delegate.boundSql.sql", sql); //重写SQL
        }
        return invocation.proceed();//实际就是调用原来的prepared方法，只是再次之前我们修改了sql
    }


    /**
     *通过Plugin的wrap(...)方法来实现代理类的生成操作
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {

        if(target instanceof StatementHandler ){
            return Plugin.wrap(target, this);//使用Plugin的wrap方法生成代理对象
        }else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties props) {
        String limitStr = props.get("page.limit").toString();
        this.limit = Integer.parseInt(limitStr);//用传递进来的参数初始化
    }

}
