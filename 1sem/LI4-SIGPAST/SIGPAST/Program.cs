using DataAccessLibrary;
using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Data;
using SIGPAST;
using Microsoft.AspNetCore.Components.Authorization;
using SIGPAST.Authentication;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();
builder.Services.AddAuthenticationCore();
builder.Services.AddScoped<AuthenticationStateProvider, AuthStateProvider>();
builder.Services.AddCascadingAuthenticationState();
builder.Services.AddTransient<ISqlDataAccess, SqlDataAccess>();
builder.Services.AddTransient<IUtilizadoresData, UtilizadoresData>();
builder.Services.AddTransient<IProdutosData, ProdutosData>();
builder.Services.AddTransient<IPassosProducaoData, PassosProducaoData>();
builder.Services.AddTransient<IIngredientesData, IngredientesData>();
builder.Services.AddTransient<IEncomendasData, EncomendasData>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();


app.UseAntiforgery();
app.UseAuthentication();
app.UseAuthorization();

app.MapStaticAssets();
app.MapRazorComponents<App>()
    .AddInteractiveServerRenderMode();

app.Run();
