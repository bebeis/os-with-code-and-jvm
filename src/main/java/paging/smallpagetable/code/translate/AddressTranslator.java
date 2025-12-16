package paging.smallpagetable.code.translate;

import paging.smallpagetable.code.model.Access;

public interface AddressTranslator {
    String name();
    TranslationResult translate(Access access);
    long pageTableBytes();
}
