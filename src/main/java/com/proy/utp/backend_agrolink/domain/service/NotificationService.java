package com.proy.utp.backend_agrolink.domain.service;

import com.proy.utp.backend_agrolink.persistance.crud.NotificacionCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.RolCrudRepository;
import com.proy.utp.backend_agrolink.persistance.crud.UsuarioCrudRepository;
import com.proy.utp.backend_agrolink.persistance.entity.Notificacion;
import com.proy.utp.backend_agrolink.persistance.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificacionCrudRepository notificacionRepository;
    private final UsuarioCrudRepository usuarioRepository;
    private final RolCrudRepository rolRepository;

    public NotificationService(
            NotificacionCrudRepository notificacionRepository,
            UsuarioCrudRepository usuarioRepository,
            RolCrudRepository rolRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    /**
     * Crea una notificación para un usuario específico
     */
    @Transactional
    public void createNotification(Usuario usuario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setLeido(false);
        notificacionRepository.save(notificacion);
    }

    /**
     * Crea notificaciones para todos los administradores
     */
    @Transactional
    public void notifyAdmins(String mensaje) {
        // Usamos el método del repositorio que ya busca por nombre de rol
        List<Usuario> admins = usuarioRepository.findByRoles_Nombre("ADMINISTRADOR");
        for (Usuario admin : admins) {
            createNotification(admin, mensaje);
        }
    }

    /**
     * Obtiene todas las notificaciones de un usuario
     */
    public List<Notificacion> getNotificationsForUser(Long userId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
    }

    /**
     * Marca una notificación como leída
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificacionRepository.findById(notificationId).ifPresent(notif -> {
            notif.setLeido(true);
            notificacionRepository.save(notif);
        });
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    @Transactional
    public void markAllAsReadForUser(Long userId) {
        List<Notificacion> notifications = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
        for (Notificacion notif : notifications) {
            if (!notif.isLeido()) {
                notif.setLeido(true);
                notificacionRepository.save(notif);
            }
        }
    }

    /**
     * Elimina una notificación
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificacionRepository.deleteById(notificationId);
    }

    /**
     * Elimina todas las notificaciones de un usuario
     */
    @Transactional
    public void deleteAllForUser(Long userId) {
        List<Notificacion> notifications = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
        notificacionRepository.deleteAll(notifications);
    }
}
