## Stand-in

Stand-in is a plugin all about manipulating entities for visual effects.

## Features

### Entity replacement

Replace armor stands to other entities such as mannequins and (in the future) display entities.

`standin.change_type.<entity_type>` - Permission that allows changing to specified entity type (only if it's supported).

Middle click (pick entity action) on most entities will display a dialog allowing for changing entity type. This
currently [will NOT work for mannequins](https://github.com/PaperMC/Paper/issues/13340). Alternatively you can open the
edit menu with `shift + right click` on the entity and choosing "Change type" option.


### Entity manipulation

Manipulate properties of entities, such as setting player profiles on mannequins, making armor stands invisible and more.

Edit menu can be opened with `shift + right click` on the entity.

`standin.edit.<entity_type>` - Permission that allows editing specified entity type (only if it's supported.).

## Future

Plugin is currently in early development and will evolve to include more features such as:
- Manipulating armor stand parts individually
- Preset armor stand poses
- Allowing more entity types for replacement (text display, item display, block display)
- Possibly simple AI for mannequins (big question mark on this one)
- Whatever gets suggested and will not be pain to implement
