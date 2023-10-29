package pepjebs.disastrousfires.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pepjebs.disastrousfires.DisastrousFiresMod;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Mixin(Entity.class)
public class FireHelmetReductionMixin {

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    public boolean applyFireDamageReduction(Entity e, DamageSource ds, float f) {
        if (isEntityWearingFireHelmet(e)) {
            float multiplier = (100 - DisastrousFiresMod.CONFIG.fireTickDamageReductionPct) / 100.0f;
            return e.damage(ds, multiplier * f);
        } else {
            return e.damage(ds, f);
        }
    }

    private boolean isEntityWearingFireHelmet(Entity e) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(e.getArmorItems().iterator(),
                Spliterator.ORDERED), false).anyMatch(i -> i.getItem() ==
                Registries.ITEM.get(DisastrousFiresMod.FIRE_HELMET));
    }
}
