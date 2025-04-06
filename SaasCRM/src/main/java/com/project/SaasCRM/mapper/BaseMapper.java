package com.project.SaasCRM.mapper;

import java.util.List;
import java.util.Set;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDtoList(List<E> entities);
    List<E> toEntityList(List<D> dtos);
    Set<D> toDtoSet(Set<E> entities);
    Set<E> toEntitySet(Set<D> dtos);
} 