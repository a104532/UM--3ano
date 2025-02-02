using DataAccessLibrary.Models;

namespace DataAccessLibrary.Interfaces
{
    public interface IUtilizadoresData
    {
        Task<List<Utilizador>> GetAll();
        Task insertUtilizador(Utilizador utilizador);
        Task<Utilizador> findUtilizador(string email);
    }
}