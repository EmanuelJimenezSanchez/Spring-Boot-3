package med.voll.api.domain.consulta.desafio;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosCancelamientoConsulta;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorCancelamientoDeConsulta {

  @Autowired
  private ConsultaRepository repository;

  @Override
  public void validar(DatosCancelamientoConsulta datos) {
    var consulta = repository.getReferenceById((datos.idConsulta()));
    var ahora = LocalDateTime.now();
    var diferenciaEnHoras = Duration.between(ahora, consulta.getFecha()).toHours();

    if (diferenciaEnHoras < 30) {
      throw new ValidationException("Consulta solamente puede ser cancelada 30 minutos antes de la cita");
    }
  }

}
