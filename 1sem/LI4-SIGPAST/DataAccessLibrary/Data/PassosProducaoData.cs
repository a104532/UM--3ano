using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Models;

namespace DataAccessLibrary.Data
{
    public class PassosProducaoData : IPassosProducaoData
    {

        private readonly ISqlDataAccess _db;

        public PassosProducaoData(ISqlDataAccess db)
        {
            _db = db;
        }

        public Task<List<PassoProducao>> GetAll()
        {
            string sql = "select * from dbo.PassoProducao";

            return _db.LoadData<PassoProducao, dynamic>(sql, new { });
        }

        public async Task insertPassoProducao(PassoProducao passoProducao)
        {
            string sql = @"insert into dbo.PassoProducao (nrPasso, idProduto, texto, imagem)
                           values (@nrPasso, @idProduto, @texto, @imagem);";

            await _db.saveData(sql, passoProducao);
        }

        public async Task<List<PassoProducao>> getPassosProducaoEncomenda(int produtoId, int nrPasso)
        {
            string sql = @"select * from dbo.PassoProducao
                           where idProduto = @produtoId and nrPasso <= @nrPasso;";

            return await _db.LoadData<PassoProducao, dynamic>(sql, new { produtoId, nrPasso });
        }

        public async Task InsertIngredientePassoProducao(int idIngrediente, int nrPasso, int idProduto, float quantidade)
        {
            string sql = @"insert into dbo.IngredientePassoProducao (idIngrediente, nrPasso, idProduto, quantidade)
                           values (@idIngrediente, @quantidade, @nrPasso, @idProduto);";
            await _db.saveData(sql, new { idIngrediente, nrPasso, idProduto, quantidade });
        }
    }
}
