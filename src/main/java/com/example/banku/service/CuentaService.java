package com.example.banku.service;

import com.example.banku.exception.SaldoInsuficienteException;
import com.example.banku.model.Cuenta;
import com.example.banku.repository.CuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaService {

    private static final Logger log = LoggerFactory.getLogger(CuentaService.class);
    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll();
    }

    public Cuenta guardarCuenta(Cuenta cuenta) {
        log.info("Guardando/Actualizando cuenta N°: {}", cuenta.getNumeroCuenta());
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void realizarDeposito(String numeroCuenta, BigDecimal monto) {
        log.info("Iniciando depósito de ${} en la cuenta N°: {}", monto, numeroCuenta);
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepository.save(cuenta);
        log.info("Depósito exitoso. Nuevo saldo: ${}", cuenta.getSaldo());
    }

    @Transactional
    public void realizarRetiro(String numeroCuenta, BigDecimal monto) {
        log.info("Iniciando retiro de ${} en la cuenta N°: {}", monto, numeroCuenta);
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            log.error("Fallo de retiro en cuenta N° {}: Saldo insuficiente (${})", numeroCuenta, cuenta.getSaldo());
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar el retiro");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepository.save(cuenta);
        log.info("Retiro exitoso. Nuevo saldo: ${}", cuenta.getSaldo());
    }

    public void eliminarCuenta(Long id) {
        log.info("Eliminando cuenta con ID: {}", id);
        cuentaRepository.deleteById(id);
    }
}
