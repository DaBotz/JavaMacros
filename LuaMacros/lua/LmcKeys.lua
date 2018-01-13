keyNames = {
  [3] = '{break}',
  [8] = '{backspace}',
  [9] = '&',
  [10] = '{line_feed}',
  [12] = '{clear}',
  [13] = '{enter}',
  [16] = '+(',
  [17] = '^(',
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
  [128] = '{f17}',
  [129] = '{f18}',
  [130] = '{f19}',
  [131] = '{f20}',
  [132] = '{f21}',
  [133] = '{f22}',
  [134] = '{f23}',
  [135] = '{f24}',
  [144] = '{numlock}',
  [145] = '{scrolllock}',
  [154] = '{prtsc}',
  [155] = '{ins}',
  [156] = '{help}',
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
  [219] = '[',
  [220] = '\\' ,
  [221] = ']',
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


function getModifiersAsLetters(caller)
  local modifiers = ''

  -- TAB
  if (caller.keyStates[9] == true) then
    modifiers = modifiers .. 't'
  end

  -- SHIFT
  if (caller.keyStates[16] == true) then
    modifiers = modifiers .. 's'
  end

  -- CTRL
  if (caller.keyStates[17] == true) then
    modifiers = modifiers .. 'c'
  end

  -- ALT
  if (caller.keyStates[18] == true) then
    modifiers = modifiers .. 'a'
  end

  return modifiers
end


--------------------------------------------------------------------------------
------------- end  of #MacroRecorder direct rip-off ----------------------------
--------------------------------------------------------------------------------