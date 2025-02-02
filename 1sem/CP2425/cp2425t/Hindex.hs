module Hindex where

import Cp
import List


hindex :: [Int] -> (Int, [Int])
hindex = cataList gene . iSort
  where
    gene :: Either () (Int, (Int, [Int])) -> (Int, [Int])
    gene (Left _) = (0, [])  
    gene (Right (x, (h, l)))
      | x >= length l + 1 = (length l + 1, x:l)  
      | x >= h = (h, x:l)  
      | otherwise = (h, l)

