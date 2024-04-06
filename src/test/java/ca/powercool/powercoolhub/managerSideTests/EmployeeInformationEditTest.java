package ca.powercool.powercoolhub.managerSideTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ca.powercool.powercoolhub.controllers.UserController;
import ca.powercool.powercoolhub.models.User;
import ca.powercool.powercoolhub.models.UserRole;
import ca.powercool.powercoolhub.repositories.UserRepository;

class EmployeeInformationEditTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(99L);
        testUser.setName("manager");
        testUser.setEmail("man@email");
        testUser.setRole(UserRole.MANAGER);
        testUser.setPassword("man123");
    }

    @Test
    void testUpdateEmployeeInformation() {
        when(userRepository.findByEmail(anyString())).thenReturn(testUser);
        User sampleUser = userRepository.findByEmail(testUser.getEmail());
        sampleUser.setName("changedName");
        sampleUser.setRole(UserRole.TECHNICIAN);
        userRepository.save(sampleUser);

        User updatedUser = userRepository.findByEmail(sampleUser.getEmail());

        if (updatedUser == null) {
            System.out.println("Not such a user found in database");
        }

        assertNotNull(updatedUser);
        assertEquals("changedName", updatedUser.getName());
        assertEquals(UserRole.TECHNICIAN, updatedUser.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUserByIdAndAction_WhenUserExistsAndActionIsDelete() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockUser));

        // Act
        ResponseEntity<?> response = userController.updateEmployee(userId, userRepository.getReferenceById(userId),
                "delete");

        // Assert
        verify(userRepository, times(1)).delete(mockUser);
        assertEquals("{message=User deleted successfully}", response.getBody().toString());
    }

    @Test
    void deleteUserByIdAndAction_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("Invalid user Id:" + userId));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userController.updateEmployee(userId, userRepository.getReferenceById(userId), "delete"));
        assertEquals("Invalid user Id:" + userId, exception.getMessage());
    }
}
