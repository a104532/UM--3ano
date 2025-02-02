module Expr where


import Cp 

import Data.List

-- Definição do tipo Expr

data Expr b a = V a | N b | T Op [Expr b a] deriving (Show, Eq)
data Op = ITE | Add | Mul | Suc deriving (Show, Eq)

-- inExpr
inExpr :: Either a (Either b (Op, [Expr b a])) -> Expr b a
inExpr = either V (either N (uncurry T))

-- Base de exemplo
baseExpr :: (a1 -> b1) -> (a2 -> b2) -> (a3 -> b3) -> Either a1 (Either a2 (b4, [a3])) -> Either b1 (Either b2 (b4, [b3]))
baseExpr g h f = g -|- (h -|- (id >< map f))

-- Funções auxiliares

soma x y = T Add [x, y]
multi x y = T Mul [x,y]
ite x y z = T ITE [x, y, z]

-- pergunta 1

-- outExpr : Desdobramento do tipo Expr
outExpr :: Expr b a -> Either a (Either b (Op, [Expr b a]))
outExpr (V a) = i1 a
outExpr (N b) = i2 (i1 b)
outExpr (T op l) = i2 (i2 (op, l))

-- recExpr : Transformação recursiva

recExpr :: (a3 -> b3) -> Either b1 (Either b2 (b4, [a3])) -> Either b1 (Either b2 (b4, [b3]))
recExpr = baseExpr id id

-- cataExpr
cataExpr :: (Either b1 (Either b2 (Op, [b3])) -> b3) -> Expr b2 b1 -> b3
cataExpr g = g . recExpr (cataExpr g) . outExpr

-- anaExpr
anaExpr :: (a3 -> Either a (Either b (Op, [a3]))) -> a3 -> Expr b a
anaExpr g = inExpr . recExpr (anaExpr g) . g

-- hyloExpr
hyloExpr :: (Either b1 (Either b2 (Op, [c])) -> c) -> (a -> Either b1 (Either b2 (Op, [a]))) -> a -> c
hyloExpr h g = cataExpr h . anaExpr g


-- pergunta 2
-- Instância Functor
instance Functor (Expr b) where
   fmap f = cataExpr ( inExpr . baseExpr f id id)

-- Instância Applicative
instance Applicative (Expr b) where
    pure = return 
    (<*>) = aap 
    -- Outros casos podem ser tratados de forma similar


instance Monad (Expr b) where
    return = V
    (V a) >>= f = f a
    (N b) >>= f = N b
    (T op l) >>= f = T op (map (>>= f) l)




-- pergunta 3
mcataExpr :: Monad m => (Either a (Either b (Op, m[c])) -> m c) -> Expr b a -> m c
mcataExpr f (V a) = f (i1 a)
mcataExpr f (N b) = f (i2 (i1 b))
mcataExpr f (T op l) = f (i2 (i2 (op, mapM (mcataExpr f) l)))

-- Teste
expr :: Expr Int String
expr = T Add [N 3, T Mul [N 2, V "x"], N 5]

sumConstants :: Expr Int String -> Maybe Int
sumConstants = mcataExpr f
  where
    f (Left _)                = Just 0
    f (Right (Left b))        = Just b
    f (Right (Right (_, ms))) = fmap sum ms
{-

a função mcataExpr é um catamorfismo monádico para o tipo Expr, o que
significa que aplica uma transformação recursiva das estruturas de
árvore (Expr b a) e retorna um valor monádico (m c)

para a ultima linha : mac (mcataExpr f) l -> é recursiva e aplica 
mcataExpr f a cada elemento da lista, retornando o m[c]

-}


-- pergunta 4

f :: String -> Expr Int String
f "x" = N 0
f "y" = N 5
f _ = N 99

e :: Expr Int String
e = T ITE [V "x", N 0, T Mul [V "y", T Add [N 3, V "y"]]]


-- letExp
let_exp :: (Num c) => (a -> Expr c b) -> Expr c a -> Expr c b
let_exp f = (>>= f)
{-
let_exp f (V a) = f a
let_exp f (N b) = N b
let_exp f (T op l) = T op (map (let_exp f) l)
-}

-- pergunta 5

-- evaluate

evaluate :: (Num a, Ord a) => Expr a b -> Maybe a
evaluate (V _) = Nothing
evaluate (N b) = Just b
evaluate (T Add [x, y]) = (+) <$> evaluate x <*> evaluate y
evaluate (T Mul [x, y]) = (*) <$> evaluate x <*> evaluate y
evaluate (T ITE [cond, thenExpr, elseExpr]) =
    case evaluate cond of
      Just 0 -> evaluate elseExpr
      Just _ -> evaluate thenExpr
      Nothing -> Nothing
evaluate _ = Nothing
