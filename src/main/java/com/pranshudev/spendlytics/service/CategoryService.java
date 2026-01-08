package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.dto.CategoryDTO;
import com.pranshudev.spendlytics.entity.CategoryEntity;
import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.CategoryRepository;
import com.pranshudev.spendlytics.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service

public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private  final ModelMapper modelMapper;
    @Qualifier("patchMapper")
    private final ModelMapper patchMapper;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProfileService profileService,
            ModelMapper modelMapper, // normal ModelMapper
            @Qualifier("patchMapper") ModelMapper patchMapper // patch mapper
    ) {
        this.categoryRepository = categoryRepository;
        this.profileService = profileService;
        this.modelMapper = modelMapper;
        this.patchMapper = patchMapper;
    }
//Save Category

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),currentProfile.getId())) {
            throw new RuntimeException("Category already exists");
        }
        CategoryEntity categoryEntity = modelMapper.map(categoryDTO,CategoryEntity.class);
// 4. MANUAL LINK: Set the profile into the entity
        categoryEntity.setProfile(currentProfile);
        categoryEntity=categoryRepository.save(categoryEntity);
        return  modelMapper.map(categoryEntity,CategoryDTO.class);
    }


    //get categories for current profile/user

    public List<CategoryDTO> getCategoryForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities =
                categoryRepository.findByProfileId(profile.getId());

        return categoryEntities.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
    }

    //get categories for current profile/user by type

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities =
                categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return categoryEntities.stream().map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

    }
    //update category(patch)
    public CategoryDTO updateByCategory(Long categoryId, CategoryDTO categoryDTO) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity existingCategory =
                categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Category not found"
                                )
                        );

        // 🔥 Dynamic PATCH (no if-else)
        patchMapper.map(categoryDTO, existingCategory);
        existingCategory.setProfile(profile);

        CategoryEntity updatedCategory =
                categoryRepository.save(existingCategory);

        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    //put Full replacement
    public CategoryDTO updateCategoryPut(Long categoryId, CategoryDTO categoryDTO) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity existingCategory =
                categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Category not found"
                                )
                        );

        // ✅ PUT = full replacement
        modelMapper.map(categoryDTO, existingCategory);
        existingCategory.setProfile(profile);

        CategoryEntity updatedCategory =
                categoryRepository.save(existingCategory);

        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }





}
//
//public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
//    // Get current user's profile (do not change it!)
//    ProfileEntity profile = profileService.getCurrentProfile();
//
//    // Find the existing category belonging to this profile
//    CategoryEntity existingCategory = categoryRepository
//            .findByIdAndProfileId(categoryId, profile.getId())
//            .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));
//
//    // Update only the fields that should be editable
//    existingCategory.setName(dto.getName());
//    existingCategory.setIcon(dto.getIcon());
//
//    // Do NOT change existingCategory.profile to avoid identifier change error!
//
//    // Save updated entity
//    existingCategory = categoryRepository.save(existingCategory);
//
//    // Convert updated entity to DTO and return
//    return toDTO(existingCategory);
//}
