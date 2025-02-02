using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Models;

namespace DataAccessLibrary.Data
{
    public class IngredientesData : IIngredientesData
    {

        private readonly ISqlDataAccess _db;

        public IngredientesData(ISqlDataAccess db)
        {
            _db = db;
        }

        public Task<List<Ingrediente>> GetAll()
        {
            string sql = "select * from dbo.Ingrediente";

            return _db.LoadData<Ingrediente, dynamic>(sql, new { });
        }

        public async Task<List<Ingrediente>> GetAllAsync()
        {
            string sql = "select * from dbo.Ingrediente";

            return await _db.LoadData<Ingrediente, dynamic>(sql, new { });
        }

        public async Task insertIngrediente(Ingrediente ingrediente)
        {
            string sql = @"insert into dbo.Utilizador (designacao, unidade, quantidadeStock,imageUrl)
                           values (@designacao, @unidade, @quantidadeStock, @imageUrl);";

            await _db.saveData(sql, ingrediente);
        }

        public async Task updateQuantidadeSimples(int idIngrediente, float novaQuantidade)
        {
            string sql = @"update dbo.Ingrediente
                           set quantidadeStock = @novaQuantidade
                           where idIngrediente = @idIngrediente;";

            await _db.saveData(sql, new { idIngrediente, novaQuantidade });
        }

        public async Task updateQuantidade(int idPasso, int idProduto, List<IngredientePasso> ingredientesPasso, List <Ingrediente> todosIngredientes)
        {
            foreach (IngredientePasso ingrediente in ingredientesPasso)
            {
                Ingrediente ingredienteStock = todosIngredientes.Find(i => i.idIngrediente == ingrediente.idIngrediente);
                if (ingredienteStock != null) 
                {
                    float quantidadeARemover = ingredienteStock.QuantidadeARemover(ingrediente.quantidade);
                    string sql = @"update dbo.Ingrediente
                                   set quantidadeStock = quantidadeStock - @quantidadeARemover
                                   where idIngrediente = @idIngrediente;";        
                    await _db.saveData(sql, new { ingrediente.idIngrediente, quantidadeARemover });
                }
            }
        }

        public async Task<List<IngredientePasso>> GetIngredientesPasso(int idPasso, int idProduto)
        {
            string sql = $"select * from dbo.IngredientePassoProducao ipp where ipp.nrPasso = {idPasso} and ipp.idProduto = {idProduto};";

            return await _db.LoadData<IngredientePasso, dynamic>(sql, new { IdPasso = idPasso, IdProduto = idProduto});
        }
    }
}
