package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.dto.TaskDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper extends BaseMapper<Task, TaskDTO> {
    
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Override
    TaskDTO toDto(Task task);

    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "deal", ignore = true)
    @Override
    Task toEntity(TaskDTO dto);
} 