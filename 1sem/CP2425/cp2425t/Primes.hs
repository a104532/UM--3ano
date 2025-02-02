module Primes where 

        
import List ( anaList )
import Nat
import Exp
import Cp


primes :: Integer -> [Integer]
primes = anaList gene 

--2.1
-- gene :: N0 -> 1 + N0×N0
gene :: Integer -> Either () (Integer, Integer)
gene = (id -|- succ_id) . outN0
  where
    outN0 :: Integer -> Either () Integer
    outN0 = cond (>1) (i2) (const (i1 ())) 
    
    succ_id :: Integer -> (Integer,Integer)
    succ_id = split smallestPrimeFactor (\n -> div n (smallestPrimeFactor n))
    
    -- Função auxiliar para encontrar o menor fator primo
    smallestPrimeFactor :: Integer -> Integer
    smallestPrimeFactor n = head [x | x <- [2..n], n `mod` x == 0]

--2.2
prime_tree :: [Integer] -> Exp Integer Integer
prime_tree xs = Term 1  --mete o 1 na raiz da arvore
                        (untar -- transforma o par numa Exp tree
                        (zip  -- cria par
                        (map primes xs) xs)) --par(primos, valores iniciais)

