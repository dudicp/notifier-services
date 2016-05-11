package com.patimer.notifier.service.converter;

import com.patimer.notifier.dto.*;
import com.patimer.notifier.model.PredicateEntity;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.model.SourceWebsiteEntity;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class SearcherConverter
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Convert To Entity
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SearcherEntity convertForCreate(SearcherDto dto)
    {
        SearcherEntity entity = null;

        if(dto != null)
        {
            PredicateEntity predicateEntity = convertToEntity(dto.getPredicate());
            Set<SourceWebsiteEntity> sourceWebsiteEntities = convertToEntity(dto.getSourceWebsites());

            entity = new SearcherEntity(
                dto.getAccountId(),
                dto.getName(),
                dto.getDescription(),
                dto.getItemType().toItemType(),
                predicateEntity,
                sourceWebsiteEntities
            );
        }

        return entity;
    }

    public PredicateEntity convertToEntity(PredicateDto dto)
    {
        PredicateEntity entity = null;

        if(dto != null)
        {
            entity =
                new PredicateEntity(
                    dto.getMinPrice(),
                    dto.getMaxPrice(),
                    dto.getSellerType().toSellerType(),
                    new HashMap<>()
                );
        }

        return entity;
    }

    public SourceWebsiteEntity convertToEntity(SourceWebsiteDto dto)
    {
        SourceWebsiteEntity entity = null;

        if(dto != null)
        {
            entity =
                new SourceWebsiteEntity(
                    dto.getWebsiteType().toSourceWebsiteType(),
                    dto.getUrl(),
                    new HashMap<>()
                );
        }

        return entity;
    }

    public Set<SourceWebsiteEntity> convertToEntity(Set<SourceWebsiteDto> sourceWebsiteDtos)
    {
        Set<SourceWebsiteEntity> sourceWebsiteEntities = null;

        if(sourceWebsiteDtos != null)
        {
            sourceWebsiteEntities = new HashSet<>();
            for(SourceWebsiteDto dto : sourceWebsiteDtos)
                sourceWebsiteEntities.add(convertToEntity(dto));
        }

        return sourceWebsiteEntities;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Merge For Update
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SearcherEntity mergeForUpdate(SearcherDto dto, SearcherEntity currentEntity)
    {
        Validate.notNull(dto);
        Validate.notNull(currentEntity);

        SearcherEntity mergedEntity = new SearcherEntity(currentEntity);
        mergedEntity.setName(dto.getName());
        mergedEntity.setDescription(dto.getDescription());
        PredicateEntity predicateEntity = convertToEntity(dto.getPredicate());
        Set<SourceWebsiteEntity> sourceWebsiteEntities = convertToEntity(dto.getSourceWebsites());
        mergedEntity.setPredicateEntity(predicateEntity);
        mergedEntity.setSourceWebsites(sourceWebsiteEntities);

        return mergedEntity;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Convert To Dto
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SearcherDto convertToDto(SearcherEntity entity)
    {
        SearcherDto dto = null;

        if(entity != null)
        {
            PredicateDto predicateDto = convertToDto(entity.getPredicateEntity());
            Set<SourceWebsiteDto> sourceWebsiteDtos = convertToDto(entity.getSourceWebsites());

            dto = new SearcherDto(
                entity.getId(),
                entity.getCreatedOn(),
                entity.getModifiedOn(),
                entity.getAccountId(),
                entity.getName(),
                entity.getDescription(),
                ItemTypeDto.fromItemType(entity.getItemType()),
                predicateDto,
                sourceWebsiteDtos
            );
        }

        return dto;
    }

    public PredicateDto convertToDto(PredicateEntity entity)
    {
        PredicateDto dto = null;

        if(entity != null)
        {
            dto =
                new PredicateDto(
                    entity.getMinPrice(),
                    entity.getMaxPrice(),
                    SellerTypeDto.fromSellerType(entity.getSellerType())
                );
        }

        return dto;
    }

    public SourceWebsiteDto convertToDto(SourceWebsiteEntity entity)
    {
        SourceWebsiteDto dto = null;

        if(entity != null)
        {
            dto =
                new SourceWebsiteDto(
                    SourceWebsiteTypeDto.fromSourceWebsiteType(entity.getWebsiteType()),
                    entity.getUrl()
                );
        }

        return dto;
    }

    public Set<SourceWebsiteDto> convertToDto(Set<SourceWebsiteEntity> sourceWebsiteEntities)
    {
        Set<SourceWebsiteDto> sourceWebsiteDtos = null;

        if(sourceWebsiteEntities != null)
        {
            sourceWebsiteDtos = new HashSet<>();
            for(SourceWebsiteEntity entity : sourceWebsiteEntities)
                sourceWebsiteDtos.add(convertToDto(entity));
        }

        return sourceWebsiteDtos;
    }

}
