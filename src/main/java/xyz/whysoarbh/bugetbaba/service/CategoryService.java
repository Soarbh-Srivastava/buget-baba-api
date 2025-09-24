package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.whysoarbh.bugetbaba.dto.CategoryDTO;
import xyz.whysoarbh.bugetbaba.entity.CategoryEntity;
import xyz.whysoarbh.bugetbaba.entity.ProfileEntity;
import xyz.whysoarbh.bugetbaba.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService
{
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    //save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),profile.getId()))
        {
            throw new RuntimeException("Category already exists");
        }
        else
        {
            CategoryEntity newCategory = toEntity(categoryDTO,profile);
            newCategory = categoryRepository.save(newCategory);
            return toDTO(newCategory);
        }
    }

    public List<CategoryDTO> getCategoriesForCurrentUser()
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }
    //get catergory by type for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        categoryRepository.findByTypeAndProfileId(type,profile.getId());
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(() -> new RuntimeException("Category Not Found"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }
    //helper
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile)
    {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }
    private CategoryDTO toDTO(CategoryEntity entity)
    {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() !=null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();

    }
}
