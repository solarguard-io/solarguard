package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.*;
import org.silentsoft.solarguard.repository.*;
import org.silentsoft.solarguard.util.OrganizationUtil;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationUtil organizationUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private BundleRepository bundleRepository;

    @PreAuthorize(Authority.Has.Admin)
    public List<OrganizationEntity> getOrganizations() {
        return organizationRepository.findAll();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public OrganizationEntity getOrganization(long organizationId) {
        organizationUtil.checkMemberAuthority(organizationId);

        return organizationRepository.findById(organizationId).get();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public OrganizationEntity createOrganization(OrganizationPostVO organization) {
        if (!StringUtils.hasLength(organization.getName())) {
            throw new IllegalArgumentException("Organization name is required.");
        }

        long userId = UserUtil.getId();

        OrganizationEntity entity = new OrganizationEntity();
        entity.setName(organization.getName());
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
        entity = organizationRepository.save(entity);

        OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
        organizationMember.setId(new OrganizationMemberId(entity.getId(), userId));
        organizationMember.setRole(OrganizationMemberRole.STAFF);
        organizationMember.setCreatedBy(userId);
        organizationMember.setUpdatedBy(userId);
        organizationMemberRepository.save(organizationMember);

        return entity;
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void deleteOrganization(long organizationId) {
        organizationUtil.checkStaffAuthority(organizationId);

        // TODO delete all products, packages, bundles, etc.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<OrganizationMemberEntity> getMembers(long organizationId) {
        organizationUtil.checkMemberAuthority(organizationId);

        return organizationMemberRepository.findAllById_OrganizationId(organizationId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<OrganizationMemberEntity> addMembers(long organizationId, OrganizationMemberPostVO organizationMemberPostVO) {
        organizationUtil.checkStaffAuthority(organizationId);

        organizationMemberPostVO.getUserIds().remove(UserUtil.getId());
        organizationMemberPostVO.getUserIds().removeIf(Objects::isNull);
        organizationMemberPostVO.getUserIds().removeIf(userId -> !userRepository.findByIdAndIsDeletedFalse(userId).isPresent());

        if (organizationMemberPostVO.getUserIds().isEmpty()) {
            throw new IllegalArgumentException("User IDs are required.");
        }

        long userId = UserUtil.getId();
        List<OrganizationMemberEntity> organizationMembers = new ArrayList<>();
        for (long userIdToAdd : organizationMemberPostVO.getUserIds()) {
            OrganizationMemberId organizationMemberId = new OrganizationMemberId(organizationId, userIdToAdd);
            if (!organizationMemberRepository.existsById(organizationMemberId)) {
                OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
                organizationMember.setId(organizationMemberId);
                organizationMember.setRole(OrganizationMemberRole.MEMBER);
                organizationMember.setCreatedBy(userId);
                organizationMember.setUpdatedBy(userId);
                organizationMembers.add(organizationMember);
            }
        }

        if (organizationMembers.isEmpty()) {
            throw new IllegalArgumentException("No one to add.");
        }

        return organizationMemberRepository.saveAll(organizationMembers);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public void removeMembers(long organizationId, OrganizationMemberDeleteVO organizationMemberDeleteVO) {
        organizationUtil.checkStaffAuthority(organizationId);

        organizationMemberDeleteVO.getUserIds().removeIf(Objects::isNull);

        List<OrganizationMemberId> organizationMemberIds = new ArrayList<>();
        for (long userIdToRemove : organizationMemberDeleteVO.getUserIds()) {
            OrganizationMemberId organizationMemberId = new OrganizationMemberId(organizationId, userIdToRemove);
            if (organizationMemberRepository.existsById(organizationMemberId)) {
                organizationMemberIds.add(organizationMemberId);
            }
        }

        if (organizationMemberIds.isEmpty()) {
            throw new IllegalArgumentException("No one to remove.");
        }

        organizationMemberRepository.deleteAllById(organizationMemberIds);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<ProductEntity> getProducts(long organizationId) {
        organizationUtil.checkMemberAuthority(organizationId);

        return productRepository.findAllByOrganizationId(organizationId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public ProductEntity addProduct(long organizationId, ProductPostVO productPostVO) {
        organizationUtil.checkStaffAuthority(organizationId);

        if (!StringUtils.hasLength(productPostVO.getName())) {
            throw new IllegalArgumentException("Product name is required.");
        }

        long userId = UserUtil.getId();

        ProductEntity entity = new ProductEntity();
        entity.setOrganization(getOrganization(organizationId));
        entity.setName(productPostVO.getName());
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
        entity = productRepository.save(entity);

        return entity;
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public List<PackageEntity> getPackages(long organizationId) {
        organizationUtil.checkMemberAuthority(organizationId);

        return packageRepository.findAllByOrganizationId(organizationId);
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    public PackageEntity addPackage(long organizationId, PackagePostVO packagePostVO) {
        organizationUtil.checkStaffAuthority(organizationId);

        packagePostVO.getProductIds().removeIf(Objects::isNull);

        if (!StringUtils.hasLength(packagePostVO.getName())) {
            throw new IllegalArgumentException("Package name is required.");
        } else if (packagePostVO.getProductIds().isEmpty()) {
            throw new IllegalArgumentException("Products are required.");
        }

        boolean hasDuplicateProduct = packagePostVO.getProductIds().stream().distinct().count() < packagePostVO.getProductIds().size();
        if (hasDuplicateProduct) {
            throw new IllegalArgumentException("Duplicate products are not allowed.");
        }

        List<ProductEntity> products = productRepository.findAllById(packagePostVO.getProductIds());

        boolean hasNotExistProduct = products.size() != packagePostVO.getProductIds().size();
        if (hasNotExistProduct) {
            throw new IllegalArgumentException("Some products are not exist.");
        }

        boolean hasProductNotBelongToOrganization = products.stream().anyMatch(product -> product.getOrganization().getId() != organizationId);
        if (hasProductNotBelongToOrganization) {
            throw new IllegalArgumentException("Some products are not owned by this organization.");
        }

        long userId = UserUtil.getId();

        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setOrganization(getOrganization(organizationId));
        packageEntity.setName(packagePostVO.getName());
        packageEntity.setCreatedBy(userId);
        packageEntity.setUpdatedBy(userId);
        packageEntity = packageRepository.save(packageEntity);

        for (ProductEntity product : products) {
            BundleEntity bundle = new BundleEntity();
            bundle.setId(new BundleId(packageEntity.getId(), product.getId()));
            bundle.setCreatedBy(userId);
            bundle.setUpdatedBy(userId);
            bundleRepository.save(bundle);
        }

        return packageEntity;
    }

}