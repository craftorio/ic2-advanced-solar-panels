package com.chocohead.advsolar.gui;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;

import ic2.core.block.tileentity.Ic2TileEntity;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.util.Util;

import net.minecraft.resources.ResourceLocation;

/**
 * Loads this addon's {@code guidef/*.xml} GUI definitions. IC2's {@link GuiParser} loads them via
 * {@code GuiParser.class.getResourceAsStream}, which under the Forge 1.20.1 module system only sees
 * IC2's own jar — never an addon's. So we read the XML from the tile's own classloader and feed it to
 * the private {@code GuiParser.parse(InputStream, Class)} via reflection.
 */
public final class GuiDefs {
	private static final Method GUI_PARSE_STREAM;
	static {
		try {
			Method m = GuiParser.class.getDeclaredMethod("parse", InputStream.class, Class.class);
			m.setAccessible(true);
			GUI_PARSE_STREAM = m;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Could not access IC2 GuiParser.parse(InputStream, Class)", e);
		}
	}

	private GuiDefs() {
	}

	public static GuiParser.GuiNode parse(Ic2TileEntity te) {
		ResourceLocation id = Util.getName(te.getBlockType());
		String path = String.format("/assets/%s/guidef/%s.xml", id.getNamespace(), id.getPath());
		try (InputStream raw = te.getClass().getResourceAsStream(path)) {
			if (raw == null) {
				throw new FileNotFoundException("Could not load " + path + " from the addon classpath.");
			}

			return (GuiParser.GuiNode) GUI_PARSE_STREAM.invoke(null, new BufferedInputStream(raw), te.getClass());
		} catch (Exception e) {
			throw new RuntimeException("Error reading/parsing GUI definition " + id + " from " + path, e);
		}
	}
}
