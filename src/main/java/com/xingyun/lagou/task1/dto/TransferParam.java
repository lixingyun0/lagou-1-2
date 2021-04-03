package com.xingyun.lagou.task1.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xingyun
 * @date 2021/3/30
 */
@Data
public class TransferParam {

    private String fromCardNo;

    private String toCardNo;

    private BigDecimal amount;
}
