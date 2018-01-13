function treeWalk(map,  level)
--limiter= limiter -1

--if limiter== 0 then 

--return 

--end

  if level == nil then level= 1 end

  local pad= ''
  for  i=0, level, 1 do pad= pad..'      ' end

  for key,value in pairs(map) do

  if key==nil then print (pad.. 'this key is nil?') else

      local typ=      type(value)

      if typ == 'table' then

         print (pad.. key..':= {')
         if value == nil  then
            print (pad..'   nil')
         else
                 treeWalk(value, level+1)
         end
         print (pad.. '}')

      elseif typ == 'boolean' then
              if value== true then
                   print (pad.. key..':= false')
               else
                   print (pad.. key..':= false')

               end

      elseif typ == 'function' then

          print (pad.. key..':= function ()')

      else
          print (pad.. key..':='.. value)
          end
  end       end
end

function flatWalk(map,  level)
--limiter= limiter -1

--if limiter== 0 then 

--return 

--end

  if level == nil then level= 1 end

  local pad= ''
  for  i=0, level, 1 do pad= pad..'      ' end

  for key,value in pairs(map) do

  if key==nil then print (pad.. 'this key is nil?') else

      local typ=      type(value)

      if typ == 'table' then

         print (pad.. key..':= {')
         if value == nil  then
            print (pad..'   nil')
         else
              --   treeWalk(value, level+1)
         end
         print (pad.. '}')

      elseif typ == 'boolean' then
              if value== true then
                   print (pad.. key..':= false')
               else
                   print (pad.. key..':= false')

               end

      elseif typ == 'function' then

          print (pad.. key..':= function ()')

      else
          print (pad.. key..':='.. value)
          end
  end       end
end