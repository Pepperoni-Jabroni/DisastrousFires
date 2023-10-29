package pepjebs.disastrousfires.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "disastrous_conditions")
public class DisastrousFiresConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @Comment("The % of fire damage to reduce from fire tick damage")
    public int fireTickDamageReductionPct = 100;
}
