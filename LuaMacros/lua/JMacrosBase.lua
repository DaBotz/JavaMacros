--    Version    --

--clear()
lmc.autoReload = true

require ('base64')
require ('LmcKeys')
require ('TreeWalker')

function dbg(string)
     print (string)
end

-- ---------------------------------------------------------------------
JMA={}

JMA.server_port=0
JMA.server_address='127.0.0.1'
JMA.device_scan_spot='devs'
JMA.initSpot='init'
JMA.updatesSpot='updates'
JMA.acknowledgementSpot='acknowledge'
JMA.devices={}
JMA.directCodesCache={}

JMA.set_server_address = function (srvr)
     JMA.server_address=srvr
end

JMA.set_port = function (port)
     JMA.server_port=port
end

JMA.strfind= function(s, pattern , index , plain)
     if pattern== nil then dbg('pattern is nil again')
          return -1

     end

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
     local  upd, updateCode, rvlen, updateIndex

     upd= 'updates '
     rvlen= string.len(rv)

     updateIndex =  JMA.strfind (rv, 'updates ' ,0,true)
     dbg('updateIndex was'.. updateIndex)

     if( updateIndex < 0 )  then
          print ('No Updates')
     else
          print ('Updating--- ')
          updateIndex= updateIndex + string.len(upd)
          updateCode= string.sub(rv, updateIndex, rvlen)
          JMA.update(updateCode )
          print ('                 --- finished updating')
     end

end



JMacros_base_handler= function(device,  scanCode, direction, deviceType)

     if device.autonomousNumLock and scanCode==144 then
          return
     end

     if(direction==0) then
     else
          direction=1
     end

     local modsAsLetters
     modsAsLetters = getModifiersAsLetters(device)

     local funct;
     funct= device.directFunctions[scanCode]

     if funct == nil
     then
          funct = JMA.getdDirect(device.basicId, scanCode)
          device.directFunctions[scanCode]=funct
     end


     if funct== false or funct == nil  then

     -- do nothing

     else
          if   funct(device, scanCode, direction, deviceType) == true
          then
               return
          end
     end

     if(device.directions[scanCode]== direction) then
          return
     end

     device.directions[scanCode]= direction
     device.keyStates[scanCode]= (direction==1)


     if device.enabled then
          local request,  updateIndex,  rv

          request = 'http://'..JMA.server_address..':'..JMA.server_port..'/'..  '?'
          request = request .. 'source=' .. device.basicId
          request = request .. '&k=' .. scanCode
          request = request .. '&d=' .. direction
          request = request .. '&mods=' .. modsAsLetters
          request = request .. '&t=1'

          print('request '..request)
          rv=lmc_http_get(request, 1)

          if ( JMA.strfind (rv, 'OK',0,true)  < 0  ) then
               print ('error: ')
               print(rv)
          else
               JMA.readUpdates(rv)
          end
     else
          JMA.sendKeys(getModifiers(device), getKeyName(scanCode))
     end
end


JMA.sendKeys = function (modifiers, text)
     if modifiers == nil or modifiers == "" then
     else
          text=modifiers..text..')'
     end
     lmc_send_keys(text)
     print(text)
end


JMA.loadDirectCode= function  (filename)
     filename=base64.dec(filename)
     
     dbg("JMA.loadDirectCode( "..filename)

     local f = assert(loadfile(filename))
     return f()
end

-- JMA.exec=function (line)
--   if (JMA.strfind(line, '%a') < 0 ) then return end

--   local command= string.gsub(line, "([%w_]+)%W.*", '%1')
--   local id = string.gsub(line, "([%w_]+)%W+([%w_]+)%W+", '%2')
--   local errorcode= nil

--   if(JMA[command]==nil) then
--     errorcode = "Command JMA."..command.. ' does not exist'
--   elseif (JMA[command]== JMA.exec) then
--     errorcode = "Use of the JMA."..command.. ' command is not allowed'
--   elseif (id== line) then
--     JMA[command]()
--   else
--     JMA[command](id)
--   end
--   if( errorcode == nil ) then
--     dbg(line)
--   else
--     local diesis = string.rep('#', 60)
--     print (diesis)
--     print(errorcode)
--     print (diesis)
--     print()
--   end
--   return errorcode
-- end



JMA.exec=function (line)
     if (JMA.strfind(line, '%a') < 0 ) then return end

     local command= string.gsub(line, "([%w_]+)%W.*", '%1')
     local id = string.gsub(line, "([%w_]+)%W+([%w_]+)%W+", '%2')

     dbg('>>'..command..'<<, line 197')

     local errorcode= nil
     local indexOfP= JMA.strfind(line, ')')
     if indexOfP >0 then
          line= string.sub(line, 1, indexOfP)
     end
     print(line)


     if(command=='updates') then
     --    print('Update -')
     elseif(command=='nothing') then
          print('No updating was required')
     elseif(JMA[command]==nil) then
          errorcode = "Command JMA."..command.. ' does not exist'
     elseif (JMA[command]== JMA.exec) then
          errorcode = "Use of the JMA."..command.. ' command is not allowed'

     elseif (JMA[command]== JMA.configureDevice ) then

          dbg('JMA.exec uses load-string')
          local func = assert(loadstring(" JMA." .. line.."  "))
          if( func == nil) then
               dbg('load-string failed!')
          else
               func()
          end

     elseif (id== line) then
          JMA[command]()
     else

          JMA[command](id)
     end

     if( errorcode == nil ) then
          dbg(line)
          if line == nil then
               dbg('no line')
          else
               JMA.acknowledgeUpdate(line)
          end
     else
          local diesis = string.rep('#', 60)
          print (diesis)
          print(errorcode)
          print (diesis)
          print()
     end
     return errorcode
end


JMA.print=function(text)
     print(text)
end




JMA.update= function(code)
     local old_index=1
     local index= JMA.strfind (code, '%s', old_index)
     local codelen= string.len(code)

     local line

     while ( index > 1 ) do
          line = string.sub(code , old_index, index )
          dbg(line)
          JMA.exec(line)
          old_index= index +1;
          index= JMA.strfind (code, '%s', old_index)
     end

     if(old_index < codelen ) then
          line = string.sub(code , old_index, codelen )
          JMA.exec(line)
     end
end

-- JMA.add_device = function(device)
--      if device.handled== true then
--           return
--      end
--      device.handled= true

--      lmc_device_set_name(device.basicId,device.longId);
--      local handler= function(button, direction, deviceType)
--           JMacros_base_handler(device, button, direction, deviceType)
--      end
--      lmc_set_handler( device.basicId, handler)
-- end

JMA.avoid = function(deviceid)
     local device
     device= JMA.getDevice(string.upper(deviceid));
     device.setEnabled(false)

     --  device.enabled= false;
     --  lmc_device_set_name(device.basicId ,device.longId);
end

JMA.allow= function(deviceid)
     local device
     device= JMA.getDevice(string.upper(deviceid));
     device.setEnabled(true)
     -- lmc_device_set_name(device.basicId ,device.longId);
     -- JMA.add_device(device)
end

JMA.no_num_lock = function(deviceid)
     JMA.getDevice(string.upper(deviceid)).autonomousNumLock=true
end

JMA.use_num_lock= function(deviceid)
     JMA.getDevice(string.upper(deviceid)).autonomousNumLock=false
end


-- all this params are strings... so fact off the world
JMA.configureDevice=function(id, extendedID, enabled, autonomousNumLock )
     local dev

     dbg('called JMA.configureDevice')
     dev= JMA.getDevice(id, extendedID)
     dev.enabled=enabled
     dev.autonomousNumLock= autonomousNumLock

     lmc_device_set_name(id, extendedID)

     dev.setEnabled(enabled)
end


JMA.getDevice= function (id, extendedID)
     id= string.upper (''..id)


     if JMA.devices[id]== nil then

          dbg(id)
          flatWalk(JMA.devices)



          if extendedID == nil then
               extendedID = id..'&0&'
          end


          local dev;
          dev = {}
          dev.basicId=id;
          dev.longId=extendedID
          dev.handled=false
          dev.autonomousNumLock=false


          dev.enabled=false


          dev.setEnabled= function( enable)
               dbg('called device.setEnabled(something)')

               dev.enabled=enable
               dev.attach()
          end

          dev.eventHandler= nil
          dev.keyStates={}
          dev.directions={}
          dev.directFunctions={}
          dev.attach= function ()

               dbg('called device.update for: '.. dev.basicId )
               if dev.eventHandler == nil and dev.enabled==true
               then
                    dbg('called device.update ')

                    local handler= function(button, direction, deviceType)
                         JMacros_base_handler(dev, button, direction, deviceType)
                    end

                    lmc_set_handler( dev.basicId, handler)

                    dev.eventHandler=handler
                    dev.handled=true


               elseif   dev.enabled==false then

                    if dev.eventHandler == nil then

                         dbg ( dev.basicId.. " is not attached and will keep being so"  )

                    else

                         dbg ( dev.basicId.. " asks vehemently to detach events!!!!"  )

                    end
               else
                    dbg ( dev.basicId.. " is already connected"  )

               end


               dev.setEnabled= function( enable)
                    dbg('called device.setEnabled(something)')

                    dev.enabled=enable
                    dev.attach()
               end
          end
          JMA.devices[id]=dev
     end


     return JMA.devices[id]
end

JMA.addDirectFunction= function (basicId, scanCode, func)
     JMA.getDevice(basicId).directFunctions[scanCode]=func
end

JMA.resetDirects= function()

     JMA.directCodesCache={}
     for key, device in pairs(JMA.devices) do
          device.directFunctions={}
     end

     JMA.requestUpdates()
          treeWalk(JMA.directCodesCache)
     
     -- TODO add garbage collection here
end

--JMA.acknowledge= function(commandLine)  end;

JMA.addDirect= function( basicId, scanCode, funct )
     local cache;
     local device= JMA.devices[basicId]

     if device== nil then
          cache=
               JMA.directCodesCache[basicId]
          if cache== nil then
               cache= {}
          end
     else
          cache=device.directFunctions
          if cache== nil then
               cache= {}
               device.directFunctions= cache
          end
     end

     JMA.directCodesCache[basicId]= cache
     cache [scanCode] = funct
end

JMA.getdDirect= function( basicId, scanCode)
     local cache;
     local device= JMA.devices[basicId]

     if device== nil then
          dbg(basicId..' Should have a device object in JMA')
          return false
     else
          cache=device.directFunctions
          if cache== nil then
               cache= JMA.directCodesCache[basicId]
               device.directFunctions= cache
          end
     end

     local funct =  cache [scanCode]

     if funct== nil then
          cache [scanCode] = false
          return false
     else
          return funct
     end
end



JMA.scan= function( )
    -- clear ()
     local devs = lmc_get_devices();
     local request, requestbase, rv, second= false , origCall

     requestbase= 'http://'..
          JMA.server_address..
          ':'..
          JMA.server_port..
          '/'..
          JMA.device_scan_spot..
          '?void=dm9pZA=='

     for key,value in pairs(devs) do
          request=requestbase;
          origCall=requestbase
          for key2,value2 in pairs(value) do
               request=request.."&"..key2.."="..base64.enc(""..value2);
               origCall=origCall.."&"..key2.."="..(""..value2);
          end
          -- print('request a '..request)
          print('request a '..origCall)
          rv=lmc_http_get(request, 1)
          JMA.readUpdates(rv)
          dbg (rv);
     end
     lmc_print_devices()

end

JMA.init= function(port, minimizeToTray, minimize)
     lmc.minimizeToTray = minimizeToTray ==true
     if(minimize == true ) then
          lmc_minimize()
     end

     JMA.set_port(port)

     lmc_http_server(port  + 1, function(url)

               url= string.sub(url, 2, string.len(url))

               if JMA[url]~= nil then
                    JMA[url]()
               else
                    print('Missing callback function: ' .. url)
               end
     end
     )


     JMA.scan()
end

JMA.reset= function()
     print ('Reset!')
     --lmc_reset()
     print(JMA.server_address)
     print(JMA.server_port)
     print( lmc.minimizeToTray)
end

JMA.reload= function()
     local request= 'http://'..JMA.server_address..':'..JMA.server_port..'/' .. JMA.initSpot
     print (request)
     local  rv=lmc_http_get(request, 1)

     if(rv==nil) then
          print ('JavaMacros is down')
     else
          JMA.update(rv )
     end
     lmc_print_devices()
end

JMA.nothing= function()
     dbg('The first rule: do no harm')
end

JMA.requestUpdates= function()
     -- clear()
     local request= 'http://'..JMA.server_address..':'..JMA.server_port..'/'.. JMA.updatesSpot
     dbg (request)
     local  rv=lmc_http_get(request, 1)
     dbg(rv)
     if(rv==nil) then
          print ('JavaMacros is down')
     else
          print('JavaMacros is up')
          JMA.update(rv )
     end
end


JMA.acknowledgeUpdate= function(executedCall)
    -- clear()
     local request= 'http://'..
          JMA.server_address..':'..JMA.server_port..'/'.. JMA.acknowledgementSpot..
          '?received='.. base64.enc(executedCall)
     dbg (request)
     local  rv=lmc_http_get(request, 1)
     dbg(rv)
     if(rv==nil) then
          print ('JavaMacros is down')
     else
          print('JavaMacros is up')
     end
     --  treeWalk(JMA)

end
