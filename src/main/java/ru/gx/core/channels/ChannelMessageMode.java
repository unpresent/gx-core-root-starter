package ru.gx.core.channels;

/**
 * Режим представления данных в канале - пообъектно или пакетно.
 */
public enum ChannelMessageMode {
    /**
     * В очереди каждое сообщение - это отдельный объект.
     */
    Object,

    /**
     * В очереди каждое сообщение - это пакет объектов.
     */
    Package
}