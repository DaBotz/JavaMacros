--------------------------------------------------------------------------------
--------------------- code lifted from #MacroRecorder --------------------------
--------------------------------------------------------------------------------

--[[

# MacroRecorder
Script for LuaMacros that let you record macros without programming required.

Download link:
https://github.com/mrsimb/macrorecorder/archive/master.zip

GitHub:
https://github.com/mrsimb/macrorecorder

LuaMacros:
https://github.com/me2d13/luamacros etc....

--]]

--------------------------------------------------------------------------------
------------------------------- KEY NAMES SECTION ------------------------------
--------------------------------------------------------------------------------

keyNames = {
  [8] = '{backspace}',
  [9] = '&(',
  [13] = '{enter}',
  [16] = '+(',
  [17] = '^(',
  [18] = '%(',
  [19] = '{pause}',
  [20] = '{capslock}',
  [27] = '{escape}',
  [32] = ' ',
  [33] = '{pgup}',
  [34] = '{pgdn}',
  [35] = '{end}',
  [36] = '{home}',
  [37] = '{left}',
  [38] = '{up}',
  [39] = '{right}',
  [40] = '{down}',
  [44] = '{prtsc}',
  [45] = '{ins}',
  [46] = '{del}',
  [48] = '0',
  [49] = '1',
  [50] = '2',
  [51] = '3',
  [52] = '4',
  [53] = '5',
  [54] = '6',
  [55] = '7',
  [56] = '8',
  [57] = '9',
  [65] = 'a',
  [66] = 'b',
  [67] = 'c',
  [68] = 'd',
  [69] = 'e',
  [70] = 'f',
  [71] = 'g',
  [72] = 'h',
  [73] = 'i',
  [74] = 'j',
  [75] = 'k',
  [76] = 'l',
  [77] = 'm',
  [78] = 'n',
  [79] = 'o',
  [80] = 'p',
  [81] = 'q',
  [82] = 'r',
  [83] = 's',
  [84] = 't',
  [85] = 'u',
  [86] = 'v',
  [87] = 'w',
  [88] = 'x',
  [89] = 'y',
  [90] = 'z',
  [96] = '{num0}',
  [97] = '{num1}',
  [98] = '{num2}',
  [99] = '{num3}',
  [100] = '{num4}',
  [101] = '{num5}',
  [102] = '{num6}',
  [103] = '{num7}',
  [104] = '{num8}',
  [105] = '{num9}',
  [106] = '{nummultiply}',
  [107] = '{numplus}',
  [109] = '{numminus}',
  [110] = '{numdecimal}',
  [111] = '{numdivide}',
  [112] = '{f1}',
  [113] = '{f2}',
  [114] = '{f3}',
  [115] = '{f4}',
  [116] = '{f5}',
  [117] = '{f6}',
  [118] = '{f7}',
  [119] = '{f8}',
  [120] = '{f9}',
  [121] = '{f10}',
  [122] = '{f11}',
  [123] = '{f12}',
  [124] = '{f13}',
  [125] = '{f14}',
  [126] = '{f15}',
  [127] = '{f16}',
  [144] = '{numlock}',
  [145] = '{scrolllock}',
  [160] = '+<(',
  [161] = '+>(',
  [162] = '^<(',
  [163] = '^>(',
  [164] = '%<(',
  [165] = '%>(',
  [186] = ';',
  [187] = '=',
  [188] = ',',
  [189] = '-',
  [190] = '.',
  [191] = '/',
  [192] = '`',
  [220] = '\\',
  [221] = ']',
  [219] = '[',
  [222] = '\''
}

function getKeyName(scanCode)
  if (keyNames[scanCode] ~= nil) then
    return keyNames[scanCode]
  end
  return nil
end

--------------------------------------------------------------------------------
------------------------------- Modifiers SECTION ------------------------------
--------------------------------------------------------------------------------


function getModifiers(caller)
  local modifiers = ''

  -- TAB
  if (caller.keyStates[9] == true) then
    modifiers = modifiers .. getKeyName(9)
  end

  -- SHIFT
  if (caller.keyStates[16] == true) then
    modifiers = modifiers .. getKeyName(16)
  end

  -- CTRL
  if (caller.keyStates[17] == true) then
    modifiers = modifiers .. getKeyName(17)
  end

  -- ALT
  if (caller.keyStates[18] == true) then
    modifiers = modifiers .. getKeyName(18)
  end

  return modifiers
end

--------------------------------------------------------------------------------
------------- end  of #MacroRecorder direct rip-off ----------------------------
--------------------------------------------------------------------------------


-- ---------------------------------------------------------------------
JMA={}
JMA.devices_to_avoid={}
JMA.autonomous_num_lock={}
JMA.server_port=0
JMA.server_address='127.0.0.1'
JMA.deviceSpot='dev'
JMA.initSpot='init'
JMA.handled={}

JMA.to_unicode = function(a)
a1,a2,a3,a4 = a:byte(1, -1)
ans = string.format ("%%%02X", a1)
n = a2
if (n)
then
ans = ans .. string.format ("%%%02X", n)
end
n = a3
if (n)
then
ans = ans .. string.format ("%%%02X", n)
end
n = a4
if (n)
then
ans = ans .. string.format ("%%%02X", n)
end
return ans
end

JMA.urlencode = function(str)
if (str) then
str = string.gsub (str, "\n", "\r\n")
--str = string.gsub (str, "\\", "/")
str = string.gsub (str, "([^%w ])", JMA.to_unicode)
str = string.gsub (str, " ", "+")
end
return str
end


JMA.set_server_address = function (srvr)
  JMA.server_address=srvr
end

JMA.set_port = function (port)
  JMA.server_port=port
end

 JMA.strfind= function(s, pattern , index , plain)
 if(s==nil) then
    return -1
 end
      local rv = string.find (s, pattern , index , plain)
       if( rv  == nil ) then
          return -1
       else
         return rv
       end
end

JMA.readUpdates= function(rv)
    local  upd, updateCode, rvlen
          upd= 'updates '
          rvlen= string.len(rv)

          updateIndex =  JMA.strfind (rv, upd ,0,true)

          if( updateIndex < 0 )  then
            -- print ('No Updates')
          else
             print ('Updates--- ')
             updateIndex= updateIndex + string.len(upd)
             updateCode= string.sub(rv, updateIndex, rvlen)
             JMA.update(updateCode )
             print ('---/ Updates')
          end

end

JMacros_base_handler= function(deviceNumber, states , button, direction, deviceType)
       JMA.handled[deviceNumber]=true


      if (JMA.autonomous_num_lock[deviceNumber]) then
          if (button==144) then
             return
          end
       end

       if(states.directions[button]== direction) then
             return
       end
       states.directions[button]= direction

       local request,  updateIndex,  rv

       request= 'http://'..JMA.server_address..':'..JMA.server_port..'/'..  '?'
                request=request ..'source=' .. JMA.urlencode(deviceNumber)
                request=request .. '&k=' .. button .. '&d=' .. direction  ..'&t=' ..deviceType

       rv=lmc_http_get(request, 1)
	   
	   
		if(JMA.strfind (rv, 'PASS')  == 1) then 	   
			print ('Device Ignored')
			-- Fromd macrorecorder
			lmc_send_keys(getModifiers(states) .. getKeyName(button))
			JMA.readUpdates(rv)
       elseif( JMA.strfind (rv, 'OK',0,true)  < 0  ) then
          print ('error: ')
          print(rv)
       else
          --print ('Transaction completed: ')
			JMA.readUpdates(rv)

          --  print(rv)
       end
end

JMA.exec=function (line)
      if (JMA.strfind(line, '%a') < 0 ) then return end
      local command= string.gsub(line, "([%w_]+)%W.*", '%1')
      local id = string.gsub(line, "([%w_]+)%W+([%w_]+)%W+", '%2')
      local errorcode= nil

      if(JMA[command]==nil) then
          errorcode = "Command JMA."..command.. ' does not exist'
      elseif (JMA[command]== JMA.exec) then
          errorcode = "Use of the JMA."..command.. ' command is not allowed'
      elseif (id== line) then
          JMA[command]()
      else
          JMA[command](id)
      end
      if( errorcode == nil ) then
      print(line)
      else
       local diesis = string.rep('#', 60)
       print (diesis)
       print(errorcode)
       print (diesis)
       print()
        end
      return errorcode
end



JMA.update= function(code)
    local old_index=1
    local index= JMA.strfind (code, '%s', old_index)
    local codelen= string.len(code)

    local line

    while ( index > 1 ) do
          line = string.sub(code , old_index, index )
          JMA.exec(line)
          old_index= index +1;
          index= JMA.strfind (code, '%s', old_index)
    end

    if(old_index < codelen ) then
          line = string.sub(code , old_index, codelen )
          JMA.exec(line)
    end


end

JMA.add_device = function(deviceNumber, deviceID)

      lmc_device_set_name(deviceNumber,deviceID);
      lmc_device_set_name(deviceNumber,string.lower (deviceID));

      local caller={}
      caller.directions ={}
      caller.keyStates ={}

      local handler= function(button, direction, deviceType)
            JMacros_base_handler(deviceNumber, caller, button, direction, deviceType)
      end

      lmc_set_handler( deviceNumber, handler)

end

JMA.avoid = function(id)
         JMA.devices_to_avoid[string.upper(id)]=1
end

JMA.no_num_lock = function(id)
         JMA.autonomous_num_lock[string.upper(id)]=true
end


JMA.allow= function(id)
            JMA.devices_to_avoid[string.upper(id)]=0
end

JMA.use_num_lock= function(id)
            JMA.autonomous_num_lock[string.upper(id)]=false
end

JMA.scan = function(throwaway)
      for i = 1, 255 do
      local i_d= string.upper(string.format("%02x", i) )

        if(JMA.handled[i_d]==true)  then
            print('Already managed :  ' .. i_d)
        elseif( JMA.devices_to_avoid[i_d] == 1) then
            print('Skipped id: '.. i_d)
        else

            JMA.add_device(i_d, i_d.. '&0&')
        end
    end
    lmc_print_devices()
end

JMA.init= function(port, minimizeToTray, minimize)
        JMA.set_port(port)

	lmc.minimizeToTray = minimizeToTray ==true
	if(minimize == true ) then 
		lmc_minimize()
	end

	local request= 'http://'..JMA.server_address..':'..JMA.server_port..'/init'
       -- print (request)
        local  rv=lmc_http_get(request, 1)

        if(rv==nil) then
           print ('JavaMacros is down')
        else
	        JMA.update(rv )
        end

end 



