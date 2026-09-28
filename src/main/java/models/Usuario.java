package models;

import exceptions.CredencialesInvalidasException;
import exceptions.UsuarioDuplicadoException;
import java.util.Map;
import java.util.Objects;

public abstract class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String password;
    private boolean activo;

    public Usuario(String id, String nombre, String email, String password) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("El ID no puede estar vacío.");
        this.id = id;
        setNombre(nombre);
        setEmail(email);
        setPassword(password);
        this.activo = true;
    }

    protected static void validarEmailDuplicado(String email, Map<String, Usuario> mapaUsuarios) throws UsuarioDuplicadoException {
        for (Usuario u : mapaUsuarios.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                throw new UsuarioDuplicadoException("El email " + email + " ya está registrado.");
            }
        }
    }

    public static Usuario login(String email, String password, Map<String, Usuario> mapaUsuarios) throws CredencialesInvalidasException {
        for (Usuario u : mapaUsuarios.values()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                if (!u.isActivo()) throw new CredencialesInvalidasException("El usuario está inactivo.");
                return u;
            }
        }
        throw new CredencialesInvalidasException("Email o contraseña incorrectos.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() { return id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        this.nombre = nombre; 
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }
        this.email = email; 
    }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { 
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        this.password = password; 
    }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
