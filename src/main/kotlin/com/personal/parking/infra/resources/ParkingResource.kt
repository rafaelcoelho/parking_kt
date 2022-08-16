package com.personal.parking.infra.resources

import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@RestController
@RequestMapping("/parking/v1")
@Slf4j
class ParkingResource {

    @PostMapping("/park")
    fun create(@Valid @RequestBody req: ParkRequestDto): ResponseEntity<String> {
        return ResponseEntity.ok("okay")
    }

}

@ControllerAdvice
class CustomExceptionHandler() {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handler(e: MethodArgumentNotValidException, req: WebRequest): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            """
            {
                "code": "INVALID_ARGUMENT",
                "error": ${e.bindingResult.fieldErrors.map { """{"reason": "${it.defaultMessage}"}""" }}
            }
        """.trimIndent()
        )
    }
}

data class ParkRequestDto(
    @get:NotBlank(message = "The name field must be filled up")
    val name: String,
    @get:NotBlank(message = "The openFrom field must be filled up")
    val openFrom: String,
    @get:NotBlank(message = "The closeAt field must be filled up")
    val closeAt: String,
    @get:NotNull(message = "The capacity field must be filled up")
    @get:Valid
    val capacity: CapacityDto?
)

data class CapacityDto(
    @get:Positive(message = "The total field must be positive")
    val total: Int
)