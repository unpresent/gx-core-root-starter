package ru.gxfin.common.data;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromDtoConverter<DEST extends DataObject, DESTPACKAGE extends DataPackage<DEST>, SRC extends DataObject>
        implements DtoFromDtoConverter<DEST, DESTPACKAGE, SRC> {

    protected abstract DEST getOrCreateDestinationBySource(SRC source);

    /**
     * Наполнение destination (DataObject) данными из source (DateObject).
     *
     * @param destination Объект, в который загружаем данные.
     * @param source      Объект, из которого берем данные.
     */
    @Override
    public abstract void fillDtoFromDto(DEST destination, SRC source);

    /**
     * Наполнение пакета DTOs из списка объектов источника.
     *
     * @param destination Пакет DTOs.
     * @param source      Источник - список объектов-источников.
     */
    @Override
    public void fillDtoPackageFromDtoList(DESTPACKAGE destination, Iterable<SRC> source) {
        final var destObjects = destination.getObjects();
        for (var src : source) {
            final var dest = getOrCreateDestinationBySource(src);
            fillDtoFromDto(dest, src);
            destObjects.add(dest);
        }
    }
}
