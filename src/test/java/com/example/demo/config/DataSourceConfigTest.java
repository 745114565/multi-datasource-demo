package com.example.demo.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.spec.ECParameterSpec;

import static org.junit.Assert.*;

/**
 * sun on 2017/6/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSourceConfigTest {
    //分别注入不同的数据源（jdbcTemplate）
    @Autowired
    @Qualifier("primaryJdbcTemplate")
    protected JdbcTemplate jdbcTemplatePrimary;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    protected JdbcTemplate jdbcTemplateSecond;


    @Before
    public void setUp() throws Exception {
        jdbcTemplatePrimary.update("DELETE  FROM USER ");
        jdbcTemplateSecond.update("DELETE FROM USER ");
    }
    @Test
    public void multiDataSourceTest()throws Exception{
        // 往第一个数据源中插入两条数据
        jdbcTemplatePrimary.update("insert into user(id,name,age) values(?, ?, ?)", 1, "aaa", 20);
        jdbcTemplatePrimary.update("insert into user(id,name,age) values(?, ?, ?)", 2, "bbb", 30);
        // 往第二个数据源中插入一条数据，若插入的是第一个数据源，则会主键冲突报错
        jdbcTemplateSecond.update("insert into user(id,name,age) values(?, ?, ?)", 1, "aaa", 20);


        // 查一下第一个数据源中是否有两条数据，验证插入是否成功
        Assert.assertEquals("2", jdbcTemplatePrimary.queryForObject("select count(1) from user", String.class));
        // 查一下第一个数据源中是否有两条数据，验证插入是否成功
        Assert.assertEquals("1", jdbcTemplateSecond.queryForObject("select count(1) from user", String.class));

    }

}