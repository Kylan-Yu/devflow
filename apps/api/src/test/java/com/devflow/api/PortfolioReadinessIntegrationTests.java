package com.devflow.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devflow.api.common.security.PrincipalType;
import com.devflow.api.modules.auth.entity.AdminStatus;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import com.devflow.api.modules.auth.service.JwtService;
import com.devflow.api.modules.post.entity.CategoryEntity;
import com.devflow.api.modules.post.entity.CategoryStatus;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.post.repository.CategoryRepository;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioReadinessIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void registerRefreshAndUpdateProfileFlow() throws Exception {
        RegisteredSession session = registerUser();

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "refreshToken": "%s"
                                        }
                                        """.formatted(session.refreshToken()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.id").value(session.userId()));

        String avatarUrl = "https://cdn.devflow.local/avatar-" + session.userId() + ".png";
        mockMvc.perform(
                        put("/api/v1/users/me")
                                .header("Authorization", "Bearer " + session.accessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "displayName": "Updated %s",
                                          "bio": "Full-stack builder with storage and notifications.",
                                          "avatarUrl": "%s",
                                          "preferredLanguage": "zh-CN"
                                        }
                                        """.formatted(session.username(), avatarUrl))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayName").value("Updated " + session.username()))
                .andExpect(jsonPath("$.data.avatarUrl").value(avatarUrl))
                .andExpect(jsonPath("$.data.preferredLanguage").value("zh-CN"));

        UserEntity updatedUser = userRepository.findById(session.userId()).orElseThrow();
        assertThat(updatedUser.getAvatarUrl()).isEqualTo(avatarUrl);
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void adminCanDisableUserThroughModerationApi() throws Exception {
        RegisteredSession session = registerUser();
        String adminToken = issueAdminToken();

        mockMvc.perform(
                        patch("/api/v1/admin/users/{id}/status", session.userId())
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "DISABLED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DISABLED"));

        UserEntity disabledUser = userRepository.findById(session.userId()).orElseThrow();
        assertThat(disabledUser.getStatus()).isEqualTo(UserStatus.DISABLED);

        mockMvc.perform(get("/api/v1/users/{id}", session.userId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCanHidePostThroughModerationApi() throws Exception {
        RegisteredSession session = registerUser();
        Long categoryId = createCategory();
        Long postId = createPost(session.accessToken(), session.username(), categoryId);
        String adminToken = issueAdminToken();

        mockMvc.perform(
                        patch("/api/v1/admin/posts/{id}/status", postId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "DELETED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELETED"));

        assertThat(postRepository.findById(postId).orElseThrow().getStatus()).isEqualTo(PostStatus.DELETED);

        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void reportWorkflowCanHidePostAndShowReviewedStatus() throws Exception {
        RegisteredSession reporter = registerUser();
        RegisteredSession author = registerUser();
        Long categoryId = createCategory();
        Long postId = createPost(author.accessToken(), author.username(), categoryId);

        MvcResult reportResult = mockMvc.perform(
                        post("/api/v1/posts/{id}/reports", postId)
                                .header("Authorization", "Bearer " + reporter.accessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "reason": "SPAM",
                                          "detail": "Inappropriate content that violates community guidelines."
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn();

        Long reportId = objectMapper.readTree(reportResult.getResponse().getContentAsString())
                .path("data")
                .path("id")
                .asLong();

        mockMvc.perform(
                        get("/api/v1/reports/me")
                                .header("Authorization", "Bearer " + reporter.accessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(reportId))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));

        String adminToken = issueAdminToken();
        mockMvc.perform(
                        patch("/api/v1/admin/reports/{id}", reportId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "RESOLVED",
                                          "resolutionAction": "HIDE_POST"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.resolutionAction").value("HIDE_POST"));

        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get("/api/v1/reports/me")
                                .header("Authorization", "Bearer " + reporter.accessToken())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(reportId))
                .andExpect(jsonPath("$.data[0].status").value("RESOLVED"))
                .andExpect(jsonPath("$.data[0].resolutionAction").value("HIDE_POST"))
                .andExpect(jsonPath("$.data[0].targetStatus").value("DELETED"));
    }

    @Test
    void searchPostsReturnsMatchingPublishedPosts() throws Exception {
        RegisteredSession author = registerUser();
        Long categoryId = createCategory();
        Long postId = createPost(author.accessToken(), author.username(), categoryId);

        mockMvc.perform(get("/api/v1/search/posts")
                        .param("keyword", "Community Discussion")
                        .param("categoryId", String.valueOf(categoryId))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value(postId))
                .andExpect(jsonPath("$.data.items[0].title").value("Community Discussion by " + author.username()));
    }

    @Test
    void repositoryLoadsLegacyLanguageCodesFromDatabase() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update("""
                        INSERT INTO users (
                          username,
                          email,
                          password_hash,
                          display_name,
                          bio,
                          avatar_url,
                          preferred_language,
                          role,
                          status,
                          last_login_at,
                          created_at,
                          updated_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                "migrated" + suffix,
                "migrated-" + suffix + "@devflow.local",
                "bcrypt_encoded_hash_string",
                "Migrated User " + suffix,
                "Software developer passionate about building scalable applications.",
                null,
                "en-US",
                "USER",
                "ACTIVE",
                now,
                now,
                now
        );

        UserEntity loadedUser = userRepository.findByEmailIgnoreCase("migrated-" + suffix + "@devflow.local")
                .orElseThrow();

        assertThat(loadedUser.getPreferredLanguage().value()).isEqualTo("en-US");
    }

    @Test
    void adminAuditLogsCaptureReportReviewActions() throws Exception {
        RegisteredSession reporter = registerUser();
        RegisteredSession author = registerUser();
        Long categoryId = createCategory();
        Long postId = createPost(author.accessToken(), author.username(), categoryId);

        MvcResult reportResult = mockMvc.perform(
                        post("/api/v1/posts/{id}/reports", postId)
                                .header("Authorization", "Bearer " + reporter.accessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "reason": "MISLEADING",
                                          "detail": "Content that appears to contain misleading information."
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andReturn();

        Long reportId = objectMapper.readTree(reportResult.getResponse().getContentAsString())
                .path("data")
                .path("id")
                .asLong();

        String adminToken = issueAdminToken();
        mockMvc.perform(
                        patch("/api/v1/admin/reports/{id}", reportId)
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "RESOLVED",
                                          "resolutionAction": "HIDE_POST"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/v1/admin/audit-logs")
                                .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].actionType").value("REPORT_REVIEWED"))
                .andExpect(jsonPath("$.data[0].targetType").value("POST"))
                .andExpect(jsonPath("$.data[0].targetId").value(postId))
                .andExpect(jsonPath("$.data[0].targetLabel").value("Moderation flow test by " + author.username()))
                .andExpect(jsonPath("$.data[0].nextState").value("RESOLVED"))
                .andExpect(jsonPath("$.data[0].resolutionAction").value("HIDE_POST"))
                .andExpect(jsonPath("$.data[0].contextLabel").value("Report #" + reportId));
    }

    private RegisteredSession registerUser() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String username = "user" + suffix;
        String email = suffix + "@devflow.local";

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "%s",
                                          "email": "%s",
                                          "password": "Devflow@123",
                                          "displayName": "User %s",
                                          "preferredLanguage": "en-US"
                                        }
                                        """.formatted(username, email, suffix))
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode payload = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        return new RegisteredSession(
                payload.path("user").path("id").asLong(),
                username,
                payload.path("tokens").path("accessToken").asText(),
                payload.path("tokens").path("refreshToken").asText()
        );
    }

    private String issueAdminToken() {
        LocalDateTime now = LocalDateTime.now();
        AdminUserEntity admin = new AdminUserEntity();
        admin.setUsername("admin-" + UUID.randomUUID().toString().substring(0, 6));
        admin.setPasswordHash("encoded_password_hash_for_admin_user");
        admin.setDisplayName("System Administrator");
        admin.setStatus(AdminStatus.ACTIVE);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        AdminUserEntity savedAdmin = adminUserRepository.save(admin);
        return jwtService.issueAccessToken(savedAdmin.getId(), PrincipalType.ADMIN, "ADMIN").token();
    }

    private Long createPost(String accessToken, String username, Long categoryId) throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/api/v1/posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Community Discussion by %s",
                                          "content": "Share thoughts and engage with the community on various topics.",
                                          "categoryId": %d,
                                          "tagIds": []
                                        }
                                        """.formatted(username, categoryId))
                )
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode payload = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        return payload.path("id").asLong();
    }

    private Long createCategory() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        CategoryEntity category = new CategoryEntity();
        category.setCode("demo-" + suffix);
        category.setNameZh("演示分类" + suffix);
        category.setNameEn("Demo Category " + suffix);
        category.setStatus(CategoryStatus.ACTIVE);
        category.setSortOrder(99);
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        return categoryRepository.save(category).getId();
    }

    private record RegisteredSession(
            Long userId,
            String username,
            String accessToken,
            String refreshToken
    ) {
    }
}
