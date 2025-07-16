package chloe.sprout.backend.controller

import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.UserAlreadyExistsException
import chloe.sprout.backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody request: UserSignupRequest): ResponseEntity<UserSignupResponse> {
        val response = userService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(e: UserAlreadyExistsException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
    }
}