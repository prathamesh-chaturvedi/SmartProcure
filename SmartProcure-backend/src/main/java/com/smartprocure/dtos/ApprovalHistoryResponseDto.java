package com.smartprocure.dtos;

import com.smartprocure.entities.Action;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovalHistoryResponseDto {

    private Integer approvalCycle;

    private Integer approvalLevel;

    private String approverName;

    private Action action;

    private String remarks;

}
