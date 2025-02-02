using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Components.Authorization;
using System.Security.Claims;

namespace SIGPAST.Authentication
{
    public class AuthStateProvider : AuthenticationStateProvider
    {
        private ClaimsPrincipal currentUser { get; set; }
        private readonly ClaimsPrincipal _anonymous = new(new ClaimsIdentity());

        public AuthStateProvider()
        {
            currentUser = _anonymous;
        }

        public override async Task<AuthenticationState> GetAuthenticationStateAsync()
        {
            return await Task.FromResult(new AuthenticationState(currentUser));
        }

        public void NotifyLogin(string email, string role)
        {
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Email, email),
                new Claim(ClaimTypes.Role, role)
            };
            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var principal = new ClaimsPrincipal(identity);
            currentUser = principal;
            var authState = Task.FromResult(new AuthenticationState(currentUser));
            NotifyAuthenticationStateChanged(authState);
        }

        public void NotifyLogout()
        {
            currentUser = _anonymous;
            NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentUser)));
        }
    }
}

