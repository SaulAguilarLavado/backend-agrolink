package com.proy.utp.backend_agrolink.web.controller;

import com.proy.utp.backend_agrolink.domain.User;
import com.proy.utp.backend_agrolink.domain.service.AuthenticatedUserService;
import com.proy.utp.backend_agrolink.domain.service.NotificationService;
import com.proy.utp.backend_agrolink.persistance.crud.NotificacionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Notificacion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificationController {
    private final NotificacionCrudRepository notificacionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationService notificationService;

    public NotificationController(
            NotificacionCrudRepository repo,
            AuthenticatedUserService userSvc,
            NotificationService notificationService
    ) {
        this.notificacionRepository = repo;
        this.authenticatedUserService = userSvc;
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> getMyNotifications() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return ResponseEntity.ok(notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(user.getUserId()));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead() {
        User user = authenticatedUserService.getAuthenticatedUser();
        notificationService.markAllAsReadForUser(user.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearAll() {
        User user = authenticatedUserService.getAuthenticatedUser();
        notificationService.deleteAllForUser(user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount() {
        User user = authenticatedUserService.getAuthenticatedUser();
        List<Notificacion> notifications = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(user.getUserId());
        long unreadCount = notifications.stream().filter(n -> !n.isLeido()).count();
        return ResponseEntity.ok(unreadCount);
    }
}