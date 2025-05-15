package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.trade.icesi_trade.Service.Impl.PermissionServiceImpl;
import com.trade.icesi_trade.model.Permission;
import com.trade.icesi_trade.repository.PermissionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = new Permission(1L, "CREATE_USER", "Allows creating users");
    }

    @Test
    void testFindPermissionByName_Success() {
        when(permissionRepository.findByName("CREATE_USER")).thenReturn(permission);

        Permission result = permissionService.findPermissionByName("CREATE_USER");

        assertNotNull(result);
        assertEquals("CREATE_USER", result.getName());
    }

    @Test
    void testFindPermissionByName_ThrowsException_WhenNotFound() {
        when(permissionRepository.findByName("INVALID")).thenReturn(null);

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            permissionService.findPermissionByName("INVALID");
        });

        assertEquals("Permiso no encontrado.", thrown.getMessage());
    }

    @Test
    void testFindPermissionByName_ThrowsException_WhenNameIsNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.findPermissionByName(null);
        });

        assertEquals("El nombre del permiso no puede ser nulo o vacío.", thrown.getMessage());
    }

    @Test
    void testSavePermission_Success() {
        Permission toSave = new Permission(null, "CREATE_USER", "Allows creating users");
        Permission saved = new Permission(1L, "CREATE_USER", "Allows creating users");

        when(permissionRepository.save(any(Permission.class))).thenReturn(saved);

        Permission result = permissionService.savePermission(toSave);

        assertNotNull(result);
        assertEquals("CREATE_USER", result.getName());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void testSavePermission_ThrowsException_WhenNameIsNull() {
        Permission invalid = new Permission(null, null, "desc");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.savePermission(invalid);
        });

        assertEquals("El permiso debe tener un nombre.", thrown.getMessage());
    }

    @Test
    void testSavePermission_ThrowsException_WhenNameIsEmpty() {
        Permission invalid = new Permission(null, "", "desc");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.savePermission(invalid);
        });

        assertEquals("El permiso debe tener un nombre.", thrown.getMessage());
    }

    @Test
    void testDeletePermission_Success() {
        when(permissionRepository.existsById(1L)).thenReturn(true);

        permissionService.deletePermission(1L);

        verify(permissionRepository).deleteById(1L);
    }

    @Test
    void testDeletePermission_ThrowsException_WhenNotExists() {
        when(permissionRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.deletePermission(1L);
        });

        assertEquals("El permiso no existe.", thrown.getMessage());
    }

    @Test
    void testUpdatePermission_Success() {
        Permission updated = new Permission(1L, "UPDATE_USER", "Allows updating users");

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Permission result = permissionService.updatePermission(updated, 1L);

        assertNotNull(result);
        assertEquals("UPDATE_USER", result.getName());
        assertEquals("Allows updating users", result.getDescription());
    }

    @Test
    void testUpdatePermission_ThrowsException_WhenPermissionIsNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.updatePermission(null, 1L);
        });

        assertEquals("El permiso no puede ser nulo.", thrown.getMessage());
    }

    @Test
    void testUpdatePermission_ThrowsException_WhenIdIsNull() {
        Permission valid = new Permission(1L, "READ_USER", "Desc");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.updatePermission(valid, null);
        });

        assertEquals("El ID del permiso no puede ser nulo.", thrown.getMessage());
    }

    @Test
    void testUpdatePermission_ThrowsException_WhenNameIsNull() {
        Permission invalid = new Permission(1L, null, "desc");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.updatePermission(invalid, 1L);
        });

        assertEquals("El permiso debe tener un nombre.", thrown.getMessage());
    }

    @Test
    void testUpdatePermission_ThrowsException_WhenNameIsEmpty() {
        Permission invalid = new Permission(1L, "", "desc");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            permissionService.updatePermission(invalid, 1L);
        });

        assertEquals("El permiso debe tener un nombre.", thrown.getMessage());
    }

    @Test
    void testUpdatePermission_ThrowsException_WhenNotFound() {
        Permission updated = new Permission(1L, "NEW", "desc");

        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            permissionService.updatePermission(updated, 1L);
        });

        assertEquals("Permiso no encontrado.", thrown.getMessage());
    }
}