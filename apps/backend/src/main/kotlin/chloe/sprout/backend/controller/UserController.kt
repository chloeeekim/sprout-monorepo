package chloe.sprout.backend.controller

import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserLoginResponse
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun signup(@RequestBody @Valid request: UserSignupRequest): ResponseEntity<UserSignupResponse> {
        val response = userService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: UserLoginRequest, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): ResponseEntity<UserLoginResponse> {
        val response = userService.login(request, httpRequest, httpResponse)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): ResponseEntity<Void> {
        userService.refresh(httpRequest, httpResponse)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        userService.logout(request)
        return ResponseEntity.ok().build()
    }
}