using DataAccessLibrary.Models;

namespace DataAccessLibrary.Interfaces
{
    public interface IPassosProducaoData
    {
        Task<List<PassoProducao>> GetAll();
        Task insertPassoProducao(PassoProducao passoProducao);
        Task<List<PassoProducao>> getPassosProducaoEncomenda(int produtoId, int nrPasso);

        Task InsertIngredientePassoProducao(int idIngrediente, int nrPasso, int idProduto, float quantidade);
    }
}