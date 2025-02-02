using DataAccessLibrary.Models;

namespace DataAccessLibrary.Interfaces
{
    public interface IEncomendasData
    {
        Task<List<Encomenda>> GetAll();
        Task insertEncomenda(Encomenda encomenda);
        Task<List<Encomenda>> GetEncomendasCliente(string emailCliente);
        Task<Encomenda> GetEncomenda(int id);
        void UpdateEstadoEncomenda(int id, string estado);
        void UpdateNrPassoEncomenda(int id, int nrPasso);
    }
}