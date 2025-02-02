module Convolution where

import Cp

import List

-- Gera a matriz usando anaList
-- O estado tem que conter: lista1, lista2, linha atual


 
type State a = ([a], [a], Int)

-- Função geradora para o anaList
matrixGen :: Num a => State a -> Either () ([a], State a)
matrixGen (xs, ys, i)
    | i >= length ys  = i1 ()  -- Termina quando processou todas as linhas
    | otherwise = i2 (row, (xs, ys, i + 1))  -- Continua para próxima linha
    where row = [x * (ys !! i) | x <- xs] 

getDiagonal :: Int -> [[a]] -> [a]
getDiagonal k matrix 
    | k < 0     = [] -- para prevenir índices negativos
    | otherwise = [matrix !! i !! j 
      | i <- [0..rows-1], j <- [0..cols-1], i + j == k]
    where
        rows = length matrix
        cols = length (head matrix)

sumDiagonals :: Num a => [[a]] -> [a]
sumDiagonals matrix = map (\k -> sum (getDiagonal k matrix)) [0..(rows + cols - 2)]
    where
        rows = length matrix
        cols = length (head matrix)

-- Função principal de convolução usando analist
convolve :: Num a => [a] -> [a] -> [a]
convolve xs ys = sumDiagonals (anaList matrixGen (xs, ys, 0) )


