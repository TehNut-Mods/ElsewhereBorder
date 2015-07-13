package tehnut.elsewhere.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.apache.commons.lang3.tuple.Pair;

import tehnut.elsewhere.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static org.lwjgl.opengl.GL11.*;

public class EventHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        double coordX = event.player.posX;
        double coordY = event.player.posY;
        double coordZ = event.player.posZ;

        EntityPlayer player = event.player;
        int dimensionID = player.dimension;
        String outOfBounds = ConfigHandler.outOfBoundsMessage;

        if (!player.worldObj.isRemote) {
            Pair<Integer, Integer> bound = Utils.bounds.get(dimensionID);
            if (!Utils.shouldPlayerPass(player)) {
                // Too far +X
                if (coordX > bound.getLeft()) {
                    player.setPositionAndUpdate((double) bound.getLeft() - 1.5, coordY + .5, coordZ);
                    player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                }

                // Too far -X
                if (coordX < -bound.getLeft()) {
                    player.setPositionAndUpdate((double) -bound.getLeft() + 1.5, coordY + .5, coordZ);
                    player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                }

                // Too far +Z
                if (coordZ > bound.getRight()) {
                    player.setPositionAndUpdate(coordX, coordY + .5, (double) bound.getRight() - 1.5);
                    player.addChatComponentMessage(new ChatComponentText(outOfBounds));
                }

                // Too far -Z
                if (coordZ < -bound.getRight()) {
                    player.setPositionAndUpdate(coordX, coordY + .5, (double) -bound.getRight() + 1.5);
                    player.addChatComponentMessage(new ChatComponentText(ConfigHandler.outOfBoundsMessage));
                }
            }
        }
    }

    // ======= RENDERING ====== //

    enum Facing {
        XPOS,
        XNEG,
        ZPOS,
        ZNEG;

        boolean isX() {
            return this == XPOS || this == XNEG;
        }

        boolean isZ() {
            return !isX();
        }

        boolean isPos() {
            return this == XPOS || this == ZPOS;
        }

        boolean isNeg() {
            return !isPos();
        }

        static Facing[] get(EntityLivingBase renderEntity, int minX, int minZ, int maxX, int maxZ, float partialTicks) {
            double minDist = 10;
            Facing[] ret = new Facing[4];
            for (Facing f : values()) {
                double dist = f.getDist(renderEntity, minX, minZ, maxX, maxZ, partialTicks);
                if (dist < minDist) {
                    ret[f.ordinal()] = f;
                }
            }
            return ret;
        }

        double getDist(EntityLivingBase renderEntity, int minX, int minZ, int maxX, int maxZ, float partialTicks) {
            double entityX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * (double) partialTicks;
            double entityZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * (double) partialTicks;
            double ret = Double.MAX_VALUE;
            switch (this) {
            case XNEG:
                ret = entityX - minX;
                break;
            case XPOS:
                ret = entityX - maxX;
                break;
            case ZNEG:
                ret = entityZ - minZ;
                break;
            case ZPOS:
                ret = entityZ - maxZ;
                break;
            }
            return Math.abs(ret);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldRender(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase renderEntity = mc.renderViewEntity;
        double entityX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * (double) event.partialTicks;
        double entityY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * (double) event.partialTicks;
        double entityZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * (double) event.partialTicks;

        Pair<Integer, Integer> bound = Utils.bounds.get(renderEntity.dimension);

        // mins being the negative of maxes is assumed.
        // If that is ever changed, this logic needs to be altered as well.
        // The rest of the code should adapt just fine however.
        int maxX = bound.getLeft(), minX = -maxX;
        int maxZ = bound.getRight(), minZ = -maxZ;

        Facing[] facings = Facing.get(renderEntity, minX, minZ, maxX, maxZ, event.partialTicks);

        for (Facing facing : facings) {
            if (facing != null) {
                glPushMatrix();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glTranslated(-entityX, -entityY, -entityZ);
                double transX = facing.isX() ? facing.isPos() ? maxX - 0.001 : minX + 0.001 : entityX;
                double transZ = facing.isZ() ? facing.isPos() ? maxZ - 0.001 : minZ + 0.001 : entityZ;
                glTranslated(transX, entityY - (renderEntity.height / 4), transZ);
                // Colors only
                glDisable(GL_TEXTURE_2D);
                // Smooth transition between colors
                glShadeModel(GL_SMOOTH);
                // Allow all alpha values
                glAlphaFunc(GL_GREATER, 0);
                // Allow alpha period
                glEnable(GL_BLEND);
                // Instead of figuring out how to render the circle the other direction...this
                if (facing == Facing.XPOS || facing == Facing.ZNEG) {
                    glRotatef(180, 0, 1, 0);
                }
                renderCircle(facing, renderEntity, minX, minZ, maxX, maxZ, 0.7f, event.partialTicks);
                // This one will render overtop of anything (ala nametags) so it has a much lower alpha value
                glDisable(GL_DEPTH_TEST);
                renderCircle(facing, renderEntity, minX, minZ, maxX, maxZ, 0.3f, event.partialTicks);
                glPopAttrib();
                glPopMatrix();
            }
        }
    }

    private void renderCircle(Facing facing, EntityLivingBase renderEntity, int minX, int minZ, int maxX, int maxZ, float alpha, float partialTicks) {
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(1, 0, 0, alpha);
        glVertex3f(0, 0, 0);
        glColor4f(1, 0, 0, 0);

        double radius = Math.min(2.5, (10 - facing.getDist(renderEntity, minX, minZ, maxX, maxZ, partialTicks)));
        double x2 = 0, y2, z2 = 0;
        double incr = Math.PI / 20;
        for (float angle = 0.0f; angle <= Math.PI * 2 + incr; angle += incr) {
            double v1 = Math.sin(angle) * radius;
            if (facing.isZ()) {
                x2 = v1;
            } else {
                z2 = v1;
            }
            y2 = Math.cos(angle) * radius;
            glVertex3d(x2, y2, z2);
        }
        glEnd();
    }
}