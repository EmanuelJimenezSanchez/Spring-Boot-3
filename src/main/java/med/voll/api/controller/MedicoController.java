package med.voll.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.*;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

  @Autowired
  private MedicoRepository medicoRepository;

  @PostMapping
  public ResponseEntity<DatosRespuestaMedico> registrarMedico(
      @RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
      UriComponentsBuilder uriComponentsBuilder) {
    Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
    DatosRespuestaMedico datosRespuestaMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
        medico.getEmail(), medico.getTelefono(), medico.getEspecialidad().toString(),
        new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
            medico.getDireccion().getComplemento()));

    // URI url = "http://localhost:8080/medicos/" + medico.getId();
    URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
    return ResponseEntity.created(url).body(datosRespuestaMedico);
  }

  @GetMapping
  public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 2) Pageable paginacion) {
    // return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);
    return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));
  }

  @PutMapping
  @Transactional
  public ResponseEntity<?> actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
    Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
    medico.actualizarDatos(datosActualizarMedico);
    return ResponseEntity.ok(new DatosRespuestaMedico(medico.getId(), medico.getNombre(), medico.getEmail(),
        medico.getTelefono(), medico.getEspecialidad().toString(),
        new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
            medico.getDireccion().getComplemento())));
  }

  // DELETE logico
  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<?> eliminarMedico(@PathVariable Long id) {
    Medico medico = medicoRepository.getReferenceById(id);
    medico.desactivarMedico();
    return ResponseEntity.noContent().build();
  }

  // DELETE de la base de datos
  // @DeleteMapping("/{id}")
  // @Transactional
  // public void eliminarMedico(@PathVariable Long id) {
  // Medico medico = medicoRepository.getReferenceById(id);
  // medicoRepository.delete(medico);
  // }

  @GetMapping("/{id}")
  public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
    Medico medico = medicoRepository.getReferenceById(id);
    medico.desactivarMedico();
    var datosMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(), medico.getEmail(),
        medico.getTelefono(), medico.getEspecialidad().toString(),
        new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
            medico.getDireccion().getComplemento()));
    return ResponseEntity.ok(datosMedico);
  }
}