package chloe.sprout.backend.auth

import chloe.sprout.backend.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {
    fun getUserId() = user.id
    override fun getUsername() = user.email
    override fun getPassword() = user.password
    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}